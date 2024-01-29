package com.askfar.fakepaymentprovider.service;

import com.askfar.fakepaymentprovider.model.Transaction;

public interface WebhookService {

    void notificationService(Transaction transaction);
}