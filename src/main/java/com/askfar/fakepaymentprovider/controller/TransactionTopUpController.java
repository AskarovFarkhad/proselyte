package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.dto.TransactionTopUpResponseDto;
import com.askfar.fakepaymentprovider.security.SecurityService;
import com.askfar.fakepaymentprovider.service.impl.TransactionTopUpServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payments/transaction")
public class TransactionTopUpController {

    private final TransactionTopUpServiceImpl topUpService;

    private final SecurityService securityService;

    @GetMapping("/{transactionId}/details")
    public Mono<TransactionTopUpResponseDto> findTransactionDetails(@RequestHeader("Authorization") String authorization, @PathVariable UUID transactionId) {
        if (securityService.authorization(authorization)) {
            return topUpService.findTransactionDetails(transactionId);
        }
        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/list")
    public Mono<Page<TransactionTopUpResponseDto>> findTransactionAll(
            @RequestHeader("Authorization") String authorization,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Pageable pageable) {
        if (securityService.authorization(authorization)) {
            if (nonNull(startDate) && nonNull(endDate)) {
                return topUpService.findTransactionAll(startDate, endDate, pageable);
            } else {
                return topUpService.findTransactionAll(pageable);
            }
        }
        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
    }

    // TODO implement POST method
}
