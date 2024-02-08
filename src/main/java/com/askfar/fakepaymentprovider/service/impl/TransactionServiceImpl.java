package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.enums.TransactionMessage;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.exception.NotFoundException;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.model.Wallet;
import com.askfar.fakepaymentprovider.repository.CardRepository;
import com.askfar.fakepaymentprovider.repository.CustomerRepository;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.repository.WalletRepository;
import com.askfar.fakepaymentprovider.service.TransactionService;
import com.askfar.fakepaymentprovider.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CardRepository cardRepository;

    private final WebhookService webhookService;

    private final WalletRepository walletRepository;

    private final TransactionMapper transactionMapper;

    private final CustomerRepository customerRepository;

    private final TransactionRepository transactionRepository;

    private final TransactionalOperator transactionalOperator;

    private static final Wallet EMPTY_WALLET = new Wallet();

    @Override
    public Mono<TransactionResponseDto> findTransactionDetails(UUID transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transaction by transactionId=%s not found", transactionId))))
                                    .doOnSuccess(dto -> log.info("Transaction with transactionId = {} found: {}", transactionId, dto))
                                    .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Page<TransactionResponseDto>> findTransactionAll(Pageable pageable, TransactionType type) {
        return transactionRepository.findAllByToday(pageable, type)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions by %s not found", LocalDate.now()))))
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByToday(type))
                                    .as(transactionalOperator::transactional)
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    public Mono<Page<TransactionResponseDto>> findTransactionAll(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, TransactionType type) {
        return transactionRepository.findAllByQueryDate(startDate, endDate, pageable, type)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions from %s to %s not found", startDate, endDate))))
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByQueryDate(startDate, endDate, type))
                                    .as(transactionalOperator::transactional)
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    public Mono<Transaction> createTransaction(TransactionRequestDto requestDto, String merchantId, TransactionType type) {
        Transaction transaction = transactionMapper.toTransactionEntity(requestDto);
        transaction.setMerchantId(merchantId);

        return Mono.just(enrichInProgress(transaction, type))
                   .flatMap(this::saveRelations)
                   .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Void> processingTopUpTransaction() {
        return transactionRepository
                .findTransactionByTransactionTypeAndStatus(TransactionType.TOP_UP, TransactionStatus.IN_PROGRESS)
                .flatMap(this::getRelations)
                .concatMap(transaction -> walletRepository.findByMerchantIdAndCurrency(transaction.getMerchantId(), transaction.getCurrency().name())
                                                          .defaultIfEmpty(EMPTY_WALLET)
                                                          .flatMap(wallet -> {
                                                              if (wallet.getCurrency() != null) {
                                                                  wallet.addAmount(transaction.getAmount());
                                                                  return walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                                                         .then(enrichSuccessStatus(transaction));
                                                              }
                                                              return enrichInCurrencyNotAllowedStatus(transaction);
                                                          }))
                .as(transactionalOperator::transactional)
                .flatMap(webhookService::notificationService)
                .then();
    }

    @Override
    public Mono<Void> processingPayOutTransaction() {
        return transactionRepository
                .findTransactionByTransactionTypeAndStatus(TransactionType.PAY_OUT, TransactionStatus.IN_PROGRESS)
                .flatMap(this::getRelations)
                .concatMap(transaction -> walletRepository.findByMerchantIdAndCurrency(transaction.getMerchantId(), transaction.getCurrency().name())
                                                          .defaultIfEmpty(EMPTY_WALLET)
                                                          .flatMap(wallet -> {
                                                              if (wallet.getCurrency() != null) {
                                                                  if (wallet.hasBalanceDepth(transaction.getAmount())) {
                                                                      wallet.subtractAmount(transaction.getAmount());
                                                                      return walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                                                             .then(enrichSuccessStatus(transaction));
                                                                  }
                                                                  return enrichInsufficientFundsStatus(transaction);
                                                              }
                                                              return enrichInCurrencyNotAllowedStatus(transaction);
                                                          }))
                .as(transactionalOperator::transactional)
                .flatMap(webhookService::notificationService)
                .then();
    }

    private Mono<Transaction> getRelations(Transaction transaction) {
        return Mono.just(transaction)
                   .zipWith(customerRepository.findById(transaction.getCustomerId()))
                   .map(result -> result.getT1().setCustomer(result.getT2()))
                   .zipWith(cardRepository.findById(transaction.getCardId()))
                   .map(result -> result.getT1().setCard(result.getT2()))
                   .as(transactionalOperator::transactional);
    }

    private Mono<Transaction> saveRelations(Transaction transaction) {
        return Mono.just(transaction)
                   .flatMap(t -> customerRepository.save(t.getCustomer())) // нужно идентификационное поле, чтобы не дублировать customer
                   .map(customer -> transaction.setCustomerId(customer.getCustomerId()))
                   .flatMap(t -> cardRepository.findByCardNumber(t.getCard().getCardNumber())
                                               .doOnNext(card -> log.info("Card with number {} already exists", card.getCardNumber()))
                                               .switchIfEmpty(cardRepository.save(t.getCard().setCustomerId(t.getCustomerId()))))
                   .map(card -> transaction.setCardId(card.getCardId()))
                   .flatMap(transactionRepository::save)
                   .doOnSuccess(t -> log.info("Transaction with transactionId={} accepted", t.getTransactionId()));
    }

    private Transaction enrichInProgress(Transaction transaction, TransactionType type) {
        return transaction.setTransactionId(UUID.randomUUID())
                          .setTransactionType(type)
                          .setStatus(TransactionStatus.IN_PROGRESS)
                          .setMessage(TransactionMessage.VALIDATED.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Mono<Transaction> enrichInCurrencyNotAllowedStatus(Transaction transaction) {
        log.info("Transaction transactionId={} ({}) was rejected due to the lack of a wallet with that currency (FAILED)",
                transaction.getTransactionId(), transaction.getTransactionType());
        transaction.setStatus(TransactionStatus.FAILED)
                   .setMessage(TransactionMessage.CURRENCY_NOT_ALLOWED.getMsg())
                   .setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private Mono<Transaction> enrichInsufficientFundsStatus(Transaction transaction) {
        log.info("Transaction transactionId={} ({}) was rejected due to insufficient funds in the wallet (FAILED)",
                transaction.getTransactionId(), transaction.getTransactionType());
        transaction.setStatus(TransactionStatus.FAILED)
                   .setMessage(TransactionMessage.INSUFFICIENT_FUNDS.getMsg())
                   .setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private Mono<Transaction> enrichSuccessStatus(Transaction transaction) {
        log.info("Transaction transactionId={} ({}) was completed successfully (SUCCESS)", transaction.getTransactionId(), transaction.getTransactionType());
        transaction.setStatus(TransactionStatus.SUCCESS)
                   .setMessage(TransactionMessage.SUCCESS.getMsg())
                   .setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }
}