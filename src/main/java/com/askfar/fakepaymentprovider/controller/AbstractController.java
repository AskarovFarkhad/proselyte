package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionCreateResponseDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.exception.BusinessUnknownException;
import com.askfar.fakepaymentprovider.security.SecurityService;
import com.askfar.fakepaymentprovider.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.nonNull;

@AllArgsConstructor
@RequestMapping("${rest.api}")
public abstract class AbstractController {

    private final SecurityService securityService;

    private final TransactionService transactionService;

    protected Mono<TransactionResponseDto> findTransactionDetails(String auth, UUID transactionId) {
        securityService.authorization(auth);
        return transactionService.findTransactionDetails(transactionId);
    }

    protected Mono<Page<TransactionResponseDto>> findTransactionAll(String auth, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate,
            TransactionType type) {

        securityService.authorization(auth);

        return nonNull(startDate) && nonNull(endDate) ? transactionService.findTransactionAll(startDate, endDate, pageable, type)
                                                      : transactionService.findTransactionAll(pageable, type);
    }

    protected Mono<ResponseEntity<TransactionCreateResponseDto>> createTransaction(String auth, TransactionRequestDto requestDto, TransactionType type) {

        String merchantId = securityService.authorization(auth);

        return transactionService.createTransaction(requestDto, merchantId, type)
                                 .switchIfEmpty(Mono.error(new BusinessUnknownException("Transaction not created")))
                                 .map(t -> {
                                     if (TransactionStatus.SUCCESS.equals(t.getStatus())) {
                                         return ResponseEntity.ok(new TransactionCreateResponseDto().setTransactionId(t.getTransactionId())
                                                                                                    .setStatus(t.getStatus())
                                                                                                    .setMessage(t.getMessage()));
                                     } else {
                                         return new ResponseEntity<>(new TransactionCreateResponseDto().setStatus(t.getStatus())
                                                                                                       .setMessage(t.getMessage()), HttpStatus.BAD_REQUEST);
                                     }
                                 });
    }
}
