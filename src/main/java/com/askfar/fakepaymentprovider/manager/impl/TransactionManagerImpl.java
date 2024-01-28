package com.askfar.fakepaymentprovider.manager.impl;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.enums.TransactionMessage;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.manager.TransactionManager;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.repository.CardRepository;
import com.askfar.fakepaymentprovider.repository.CustomerRepository;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionManagerImpl implements TransactionManager {

    private final CardRepository cardRepository;

    private final WalletRepository walletRepository;

    private final TransactionMapper transactionMapper;

    private final CustomerRepository customerRepository;

    private final TransactionRepository transactionRepository;

    @Override
    public Mono<Transaction> createTopUp(TransactionRequestDto requestDto, String merchantId) {
        return walletRepository.findByMerchantIdAndCurrency(merchantId, requestDto.getCurrency().name())
                               .flatMap(wallet -> {
                                   Transaction transaction = transactionMapper.toTransactionEntity(requestDto);
                                   transaction = enrichInProgress(transaction, TransactionType.TOP_UP);

                                   // бизнес логика, в нашем случае валидация входных данных, проверка, что существует счет с такой валютой

                                   wallet.addAmount(transaction.getAmount());
                                   return walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                          .then(Mono.just(transaction))
                                                          .flatMap(this::saveRelations);
                               })
                               .switchIfEmpty(Mono.just(transactionMapper.toTransactionEntity(requestDto))
                                                  .map(t -> enrichInCurrencyNotAllowedStatus(t, TransactionType.TOP_UP)));
    }

    @Override
    public Mono<Transaction> createPayOut(TransactionRequestDto requestDto, String merchantId) {
        return walletRepository.findByMerchantIdAndCurrency(merchantId, requestDto.getCurrency().name())
                               .flatMap(wallet -> {
                                   Transaction transaction = transactionMapper.toTransactionEntity(requestDto);
                                   transaction = enrichInProgress(transaction, TransactionType.PAY_OUT);

                                   // бизнес логика, в нашем случае валидация входных данных, проверка,
                                   // что существует счет с такой валютой и что есть средства на счету для осуществления списания

                                   if (wallet.hasBalanceDepth(transaction.getAmount())) {
                                       wallet.subtractAmount(transaction.getAmount());

                                       return walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                              .then(Mono.just(transaction))
                                                              .flatMap(this::saveRelations);
                                   }

                                   return Mono.just(transactionMapper.toTransactionEntity(requestDto))
                                              .map(this::enrichInsufficientFundsStatus);
                               })
                               .switchIfEmpty(Mono.just(transactionMapper.toTransactionEntity(requestDto))
                                                  .map(t -> enrichInCurrencyNotAllowedStatus(t, TransactionType.PAY_OUT)));
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

    private Transaction enrichInProgress(Transaction transaction, TransactionType type) {
        return transaction.setTransactionId(UUID.randomUUID())
                          .setTransactionType(type)
                          .setStatus(TransactionStatus.IN_PROGRESS)
                          .setMessage(TransactionMessage.VALIDATED.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Transaction enrichInCurrencyNotAllowedStatus(Transaction transaction, TransactionType type) {
        return transaction.setTransactionType(type)
                          .setStatus(TransactionStatus.FAILED)
                          .setMessage(TransactionMessage.CURRENCY_NOT_ALLOWED.getMsg());
    }

    private Transaction enrichInsufficientFundsStatus(Transaction transaction) {
        return transaction.setTransactionType(TransactionType.PAY_OUT)
                          .setStatus(TransactionStatus.FAILED)
                          .setMessage(TransactionMessage.INSUFFICIENT_FUNDS.getMsg());
    }
}
