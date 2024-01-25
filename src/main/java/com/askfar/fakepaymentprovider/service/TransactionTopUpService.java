package com.askfar.fakepaymentprovider.service;

import com.askfar.fakepaymentprovider.dto.TransactionTopUpResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface TransactionTopUpService {

    Mono<TransactionTopUpResponseDto> findTransactionDetails(UUID transactionId);

    Mono<Page<TransactionTopUpResponseDto>> findTransactionAll(Pageable pageable);

    Mono<Page<TransactionTopUpResponseDto>> findTransactionAll(LocalDate startDate, LocalDate enDate, Pageable pageable);
}
