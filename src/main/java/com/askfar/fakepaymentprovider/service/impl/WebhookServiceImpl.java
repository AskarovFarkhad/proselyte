package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.dto.request.TransactionWebhookRequestDto;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.model.WebhookHistory;
import com.askfar.fakepaymentprovider.repository.WebhookHistoryRepository;
import com.askfar.fakepaymentprovider.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@EnableRetry
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final WebClient webClient;

    private final TransactionMapper mapper;

    private final WebhookHistoryRepository webhookHistoryRepository;

    @Override
    @Retryable(maxAttemptsExpression = "${webclient.retryMaxAttempts}",
            backoff = @Backoff(delayExpression = "${webclient.retryMaxDelay}"), recover = "recoverNotificationService")
    public void notificationService(Transaction transaction) {
        TransactionWebhookRequestDto requestDto = mapper.toTransactionWebhookRequestDto(transaction);
        webClient.post()
                 .uri(transaction.getNotificationUrl())
                 .bodyValue(requestDto)
                 .retrieve()
                 .bodyToMono(String.class)
                 .subscribe(response -> {
                     log.info("Answer from webhook service: {}", response);
                     WebhookHistory webhookHistory = new WebhookHistory().setNotificationUrl(transaction.getNotificationUrl())
                                                                         .setTransactionId(transaction.getTransactionId())
                                                                         .setResponse(response);
                     webhookHistoryRepository.save(webhookHistory).subscribe();
                 });
    }

    @Recover
    public void recoverNotificationService(Exception e, Transaction transaction) {
        log.error("An error occurred when sending notification a transactionId={}", transaction.getTransactionId(), e);
    }
}