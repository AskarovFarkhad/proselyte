package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.dto.request.TransactionTopUpRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionCreateResponseDto;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.security.SecurityService;
import com.askfar.fakepaymentprovider.service.impl.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payments")
public class TransactionTopUpController {

    private final TransactionServiceImpl topUpService;

    private final SecurityService securityService;

    @Operation(tags = "findTransactionDetails", description = "Get transaction details", summary = "To get transaction details by TransactionId")
    @GetMapping("/transaction/{transactionId}/details")
    public Mono<TransactionResponseDto> findTransactionDetails(@RequestHeader("Authorization") String auth, @PathVariable UUID transactionId) {
        securityService.authorization(auth);
        return topUpService.findTransactionDetails(transactionId);
    }

    @GetMapping("/transaction/list")
    public Mono<Page<TransactionResponseDto>> findTransactionAll(@RequestHeader(name = "Authorization") String auth, Pageable pageable,
            @RequestParam(required = false, name = "start_date") LocalDateTime startDate,
            @RequestParam(required = false, name = "end_date") LocalDateTime endDate) {

        securityService.authorization(auth);
        if (nonNull(startDate) && nonNull(endDate)) {
            return topUpService.findTransactionAll(startDate, endDate, pageable);
        }
        return topUpService.findTransactionAll(pageable);
    }

    @PostMapping("/transaction")
    public Mono<ResponseEntity<TransactionCreateResponseDto>> createTransaction(
            @RequestHeader("Authorization") String auth, @Valid @RequestBody TransactionTopUpRequestDto requestDto) {

        securityService.authorization(auth);
        Mono<Transaction> topUpTransaction = topUpService.createTransaction(requestDto, null);
        return null;
    }
}
