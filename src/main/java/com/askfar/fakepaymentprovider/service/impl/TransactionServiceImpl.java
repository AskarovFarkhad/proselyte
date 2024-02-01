package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.enums.TransactionMessage;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.exception.NotFoundException;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.repository.CardRepository;
import com.askfar.fakepaymentprovider.repository.CustomerRepository;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.repository.WalletRepository;
import com.askfar.fakepaymentprovider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final WalletRepository walletRepository;

    private final TransactionMapper transactionMapper;

    private final CustomerRepository customerRepository;

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<TransactionResponseDto> findTransactionDetails(UUID transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transaction by transactionId=%s not found", transactionId))))
                                    .doOnSuccess(dto -> log.info("Transaction with transactionId = {} found: {}", transactionId, dto));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<TransactionResponseDto>> findTransactionAll(Pageable pageable, TransactionType type) {
        return transactionRepository.findAllByToday(pageable, type)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions by %s not found", LocalDate.now()))))
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByToday(type))
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<TransactionResponseDto>> findTransactionAll(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, TransactionType type) {
        return transactionRepository.findAllByQueryDate(startDate, endDate, pageable, type)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions from %s to %s not found", startDate, endDate))))
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByQueryDate(startDate, endDate, type))
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    @Transactional
    public Mono<Transaction> createTransaction(TransactionRequestDto requestDto, String merchantId, TransactionType type) {
        Transaction transaction = transactionMapper.toTransactionEntity(requestDto);
        transaction = enrichInProgress(transaction, type);
        return Mono.just(transaction).flatMap(this::saveRelations);
    }

    private Mono<Transaction> getRelations(Transaction transaction) {
        return Mono.just(transaction)
                   .zipWith(customerRepository.findById(transaction.getCustomerId()))
                   .map(result -> result.getT1().setCustomer(result.getT2()))
                   .zipWith(cardRepository.findById(transaction.getCardId()))
                   .map(result -> result.getT1().setCard(result.getT2()));
    }

    private Mono<Transaction> saveRelations(Transaction transaction) {
        return Mono.just(transaction)
                   .flatMap(t -> customerRepository.save(t.getCustomer()))
                   .map(customer -> transaction.setCustomerId(customer.getCustomerId()))
                   .flatMap(t -> cardRepository.findByCardNumber(t.getCard().getCardNumber())
                                               .doOnNext(card -> log.info("Card with number {} already exists", card.getCardNumber()))
                                               .switchIfEmpty(cardRepository.save(t.getCard().setCustomerId(t.getCustomerId()))))
                   .map(card -> transaction.setCardId(card.getCardId()))
                   .flatMap(transactionRepository::save)
                   .doOnSuccess(t -> log.info("Transaction with transactionId = {} accepted", t.getTransactionId()));
    }

    @Override
    public void processingTopUpTransaction() {
        transactionRepository.findTransactionByTransactionTypeAndStatus(TransactionType.TOP_UP, TransactionStatus.IN_PROGRESS)
                             .flatMap(transaction -> {
                                 return walletRepository.findByMerchantIdAndCurrency(transaction.getMerchantId(), transaction.getCurrency().name())
                                                        .flatMap(wallet -> {
                                                            wallet.addAmount(transaction.getAmount());
                                                            return walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                                                   .then(Mono.just(enrichSuccessStatus(transaction)))
                                                                                   .flatMap(transactionRepository::save);

                                                        })
                                                        .switchIfEmpty(Mono.just(transaction)
                                                                           .map(t -> enrichInCurrencyNotAllowedStatus(transaction))
                                                                           .flatMap(transactionRepository::save));
                             })
                             .subscribe();
    }

    @Override
    public void processingPayOutTransaction() {
        transactionRepository.findTransactionByTransactionTypeAndStatus(TransactionType.PAY_OUT, TransactionStatus.IN_PROGRESS)
                             .flatMap(transaction -> {
                                 return walletRepository.findByMerchantIdAndCurrency(transaction.getMerchantId(), transaction.getCurrency().name())
                                                        .flatMap(wallet -> {
                                                            if (wallet.hasBalanceDepth(transaction.getAmount())) {
                                                                wallet.subtractAmount(transaction.getAmount());
                                                                return walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                                                       .then(Mono.just(enrichSuccessStatus(transaction)))
                                                                                       .flatMap(transactionRepository::save);
                                                            }
                                                            return Mono.just(enrichInsufficientFundsStatus(transaction)).flatMap(transactionRepository::save);
                                                        })
                                                        .switchIfEmpty(Mono.just(transaction)
                                                                           .map(t -> enrichInCurrencyNotAllowedStatus(transaction))
                                                                           .flatMap(transactionRepository::save));
                             })
                             .subscribe();
    }

    private Transaction enrichInProgress(Transaction transaction, TransactionType type) {
        return transaction.setTransactionId(UUID.randomUUID())
                          .setTransactionType(type)
                          .setStatus(TransactionStatus.IN_PROGRESS)
                          .setMessage(TransactionMessage.VALIDATED.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Transaction enrichInCurrencyNotAllowedStatus(Transaction transaction) {
        return transaction.setId(null)
                          .setStatus(TransactionStatus.FAILED)
                          .setMessage(TransactionMessage.CURRENCY_NOT_ALLOWED.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Transaction enrichInsufficientFundsStatus(Transaction transaction) {
        return transaction.setId(null)
                          .setStatus(TransactionStatus.FAILED)
                          .setMessage(TransactionMessage.INSUFFICIENT_FUNDS.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Transaction enrichSuccessStatus(Transaction transaction) {
        return transaction.setId(null)
                          .setStatus(TransactionStatus.SUCCESS)
                          .setMessage(TransactionMessage.SUCCESS.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }
}