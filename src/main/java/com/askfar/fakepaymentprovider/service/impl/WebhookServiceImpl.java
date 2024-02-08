package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.dto.request.TransactionWebhookRequestDto;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.model.WebhookHistory;
import com.askfar.fakepaymentprovider.repository.WebhookHistoryRepository;
import com.askfar.fakepaymentprovider.service.WebhookService;
import com.askfar.fakepaymentprovider.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@EnableRetry
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final WebClient webClient;

    private final TransactionMapper mapper;

    private final JsonConverter jsonConverter;

    private final WebhookHistoryRepository webhookHistoryRepository;

    @Override
    @Retryable(maxAttemptsExpression = "${webclient.retryMaxAttempts}",
            backoff = @Backoff(delayExpression = "${webclient.retryMaxDelay}"), recover = "recoverNotificationService")
    public Mono<WebhookHistory> notificationService(Transaction transaction) {
        log.info("Sending a transaction notification transactionId={} ({})", transaction.getTransactionId(), transaction.getTransactionType());

        TransactionWebhookRequestDto requestDto = mapper.toTransactionWebhookRequestDto(transaction);

        return webClient.post()
                        .uri(transaction.getNotificationUrl())
                        .bodyValue(requestDto)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(response -> {
                            log.info("Answer from webhook service: {}", response);
                            WebhookHistory webhookHistory = new WebhookHistory().setNotificationUrl(transaction.getNotificationUrl())
                                                                                .setRequest(jsonConverter.getJson(requestDto))
                                                                                .setResponse(response);
                            return webhookHistoryRepository.save(webhookHistory);
                        });
    }

    @Recover
    public void recoverNotificationService(Exception e, Transaction transaction) {
        log.error("An error occurred when sending notification a transactionId={}", transaction.getTransactionId(), e);
    }
}