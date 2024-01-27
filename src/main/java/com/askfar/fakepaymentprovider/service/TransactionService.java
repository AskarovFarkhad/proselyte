package com.askfar.fakepaymentprovider.service;

import com.askfar.fakepaymentprovider.dto.request.TransactionTopUpRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionService {

    Mono<TransactionResponseDto> findTransactionDetails(UUID transactionId);

    Mono<Page<TransactionResponseDto>> findTransactionAll(Pageable pageable);

    Mono<Page<TransactionResponseDto>> findTransactionAll(LocalDateTime startDate, LocalDateTime enDate, Pageable pageable);

    Mono<Transaction> createTransaction(TransactionTopUpRequestDto requestDto, String merchantId);
}
