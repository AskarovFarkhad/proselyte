package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionCreateResponseDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.security.SecurityService;
import com.askfar.fakepaymentprovider.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class TransactionTopUpController extends AbstractController {

    @Autowired
    public TransactionTopUpController(TransactionService transactionService, SecurityService securityService) {
        super(securityService, transactionService);
    }

    @GetMapping("/transaction/{transactionId}/details")
    @Operation(tags = "findTopUpDetails", description = "Get transaction details", summary = "To get transaction details by transactionId")
    public Mono<TransactionResponseDto> findTopUpDetails(@RequestHeader("Authorization") String auth, @PathVariable UUID transactionId) {
        return super.findTransactionDetails(auth, transactionId);
    }

    @GetMapping("/transaction/list")
    @Operation(tags = "findTopUpAll", description = "Get transaction list",
            summary = "It allows to get transaction list for current day (without query parameters) or get transaction list for period (with query parameters)")
    public Mono<Page<TransactionResponseDto>> findTopUpAll(@RequestHeader(name = "Authorization") String auth, Pageable pageable,
            @RequestParam(required = false, name = "start_date") LocalDateTime startDate,
            @RequestParam(required = false, name = "end_date") LocalDateTime endDate) {
        return super.findTransactionAll(auth, pageable, startDate, endDate, TransactionType.TOP_UP);
    }

    @PostMapping("/transaction")
    @Operation(tags = "createTopUp", description = "Create transaction top up",
            summary = "Transaction method allows creating top up (deposit) transaction and charge customer payment card")
    public Mono<ResponseEntity<TransactionCreateResponseDto>> createTopUp(
            @RequestHeader("Authorization") String auth, @Valid @RequestBody TransactionRequestDto requestDto) {
        return super.createTransaction(auth, requestDto, TransactionType.TOP_UP);
    }
}
