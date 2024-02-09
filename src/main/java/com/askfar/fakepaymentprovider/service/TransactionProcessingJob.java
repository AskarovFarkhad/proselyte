package com.askfar.fakepaymentprovider.service;

public interface TransactionProcessingJob {

    void executeProcessingTopUpTransaction();

    void executeProcessingPayOutTransaction();
}
