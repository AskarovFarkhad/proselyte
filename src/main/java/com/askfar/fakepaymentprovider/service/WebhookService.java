package com.askfar.fakepaymentprovider.service;

import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.model.WebhookHistory;
import reactor.core.publisher.Mono;

public interface WebhookService {

    Mono<WebhookHistory> notificationService(Transaction transaction);
}