package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.dto.request.TransactionTopUpRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionCreateResponseDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.exception.BusinessUnknownException;
import com.askfar.fakepaymentprovider.security.SecurityService;
import com.askfar.fakepaymentprovider.service.impl.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("${rest.api}")
public class TransactionTopUpController {

    private final TransactionServiceImpl topUpService;

    private final SecurityService securityService;

    @GetMapping("/transaction/{transactionId}/details")
    @Operation(tags = "findTransactionDetails", description = "Get transaction details", summary = "To get transaction details by transactionId")
    public Mono<TransactionResponseDto> findTransactionDetails(@RequestHeader("Authorization") String auth, @PathVariable UUID transactionId) {
        securityService.authorization(auth);
        return topUpService.findTransactionDetails(transactionId);
    }

    @GetMapping("/transaction/list")
    @Operation(tags = "findTransactionAll", description = "Get transaction list",
            summary = "It allows to get transaction list for current day (without query parameters) or get transaction list for period (with query parameters)")
    public Mono<Page<TransactionResponseDto>> findTransactionAll(@RequestHeader(name = "Authorization") String auth, Pageable pageable,
            @RequestParam(required = false, name = "start_date") LocalDateTime startDate,
            @RequestParam(required = false, name = "end_date") LocalDateTime endDate) {

        securityService.authorization(auth);

        return nonNull(startDate) && nonNull(endDate) ? topUpService.findTransactionAll(startDate, endDate, pageable)
                                                      : topUpService.findTransactionAll(pageable);
    }

    @PostMapping("/transaction")
    @Operation(tags = "createTransaction", description = "Create transaction top up",
            summary = "Transaction method allows creating top up (deposit) transaction and charge customer payment card")
    public Mono<ResponseEntity<TransactionCreateResponseDto>> createTransaction(
            @RequestHeader("Authorization") String auth, @Valid @RequestBody TransactionTopUpRequestDto requestDto) {

        String merchantId = securityService.authorization(auth);

        return topUpService.createTransaction(requestDto, merchantId)
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
