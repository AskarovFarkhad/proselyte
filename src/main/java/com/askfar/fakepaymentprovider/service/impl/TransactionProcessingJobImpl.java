package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.service.TransactionProcessingJob;
import com.askfar.fakepaymentprovider.service.TransactionService;
import com.askfar.fakepaymentprovider.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class TransactionProcessingJobImpl implements TransactionProcessingJob {

    private final WebhookService webhookService;

    private final TransactionService transactionService;

    @Override
    @Scheduled(cron = "${scheduled.processingTopUpTransactionJob.cron}")
    public void executeProcessingTopUpTransaction() {
        log.info("Job 'processingTopUpTransaction' started execute: {}", LocalDateTime.now());

        transactionService.processingTopUpTransaction()
                          .doOnNext(webhookService::notificationService)
                          .subscribe();

        log.info("Job 'processingTopUpTransaction' finished: {}", LocalDateTime.now());
    }

    @Override
    //@Scheduled(cron = "${scheduled.processingPayOutTransactionJob.cron}")
    public void executeProcessingPayOutTransaction() {
        log.info("Job 'processingPayOutTransaction' started execute: {}", LocalDateTime.now());

        transactionService.processingPayOutTransaction()
                          .doOnNext(webhookService::notificationService)
                          .subscribe();

        log.info("Job 'processingPayOutTransaction' finished: {}", LocalDateTime.now());
    }
}
