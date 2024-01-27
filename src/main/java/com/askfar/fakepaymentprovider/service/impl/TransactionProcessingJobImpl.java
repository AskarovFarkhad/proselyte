package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.service.TransactionProcessingJob;
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

    private final TransactionRepository transactionRepository;

    @Override
    @Scheduled(cron = "${scheduled.cron}")
    public void execute() {
        log.info("Job TransactionProcessing started execute: {}", LocalDateTime.now());



        log.info("Job TransactionProcessing finished execute: {}", LocalDateTime.now());
    }
}
