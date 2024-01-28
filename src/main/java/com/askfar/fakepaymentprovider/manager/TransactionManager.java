package com.askfar.fakepaymentprovider.manager;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.model.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionManager {

    Mono<Transaction> createTopUp(TransactionRequestDto requestDto, String merchantId);

    Mono<Transaction> createPayOut(TransactionRequestDto requestDto, String merchantId);
}
