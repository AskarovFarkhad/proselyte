package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.enums.TransactionMessage;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.repository.WalletRepository;
import com.askfar.fakepaymentprovider.service.TransactionProcessingJob;
import com.askfar.fakepaymentprovider.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class TransactionProcessingJobImpl implements TransactionProcessingJob {

    private final WebhookService webhookService;

    private final WalletRepository walletRepository;

    private final TransactionRepository transactionRepository;

    @Override
    @Scheduled(cron = "${scheduled.cron}")
    public void execute() {
        log.info("Job TransactionProcessing started execute: {}", LocalDateTime.now());
        // TODO implement me
        transactionRepository.findTransactionByStatus(TransactionStatus.IN_PROGRESS)
                             .doOnNext(transaction -> {
                                 walletRepository.findByMerchantIdAndCurrency(transaction.getMerchantId(), transaction.getCurrency().name())
                                                 .flatMap(wallet -> {
                                                     Mono<Transaction> mono;
                                                     if (TransactionType.TOP_UP.equals(transaction.getTransactionType())) {
                                                         wallet.addAmount(transaction.getAmount());
                                                         mono = walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                                                .then(Mono.just(enrichSuccessStatus(transaction)));
                                                     } else {
                                                         if (wallet.hasBalanceDepth(transaction.getAmount())) {
                                                             wallet.subtractAmount(transaction.getAmount());
                                                             mono = walletRepository.updateWalletByWalletId(wallet.getWalletId(), wallet.getBalance())
                                                                                    .then(Mono.just(enrichSuccessStatus(transaction)));
                                                         }
                                                         mono = Mono.just(transaction).map(this::enrichInsufficientFundsStatus);
                                                     }
                                                     return mono;
                                                 })
                                                 .switchIfEmpty(Mono.just(transaction).map(t -> enrichInCurrencyNotAllowedStatus(transaction)));
                             })
                             .subscribe();
        log.info("Job TransactionProcessing finished execute: {}", LocalDateTime.now());
    }

    private Transaction enrichInCurrencyNotAllowedStatus(Transaction transaction) {
        return transaction.setStatus(TransactionStatus.FAILED)
                          .setMessage(TransactionMessage.CURRENCY_NOT_ALLOWED.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Transaction enrichInsufficientFundsStatus(Transaction transaction) {
        return transaction.setStatus(TransactionStatus.FAILED)
                          .setMessage(TransactionMessage.INSUFFICIENT_FUNDS.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Transaction enrichSuccessStatus(Transaction transaction) {
        return transaction.setStatus(TransactionStatus.SUCCESS)
                          .setMessage(TransactionMessage.SUCCESS.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }

    private Transaction enrichFailedStatus(Transaction transaction) {
        return transaction.setStatus(TransactionStatus.FAILED)
                          .setMessage(TransactionMessage.PAYOUT_MIN_AMOUNT.getMsg())
                          .setUpdatedAt(LocalDateTime.now());
    }
}
