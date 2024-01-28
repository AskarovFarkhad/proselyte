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
public class TransactionPayOutController extends AbstractController {

    @Autowired
    public TransactionPayOutController(TransactionService transactionService, SecurityService securityService) {
        super(securityService, transactionService);
    }

    @GetMapping("/payout/{payoutId}/details")
    @Operation(tags = "findPayOutDetails", description = "Get pay out details", summary = "To get pay out details by payoutId")
    public Mono<TransactionResponseDto> findPayOutDetails(@RequestHeader("Authorization") String auth, @PathVariable UUID payoutId) {
        return super.findTransactionDetails(auth, payoutId);
    }

    @GetMapping("/payout/list")
    @Operation(tags = "findPayOutAll", description = "Get pay out list",
            summary = "It allows to get pay out list for current day (without query parameters) or get pay out list for period (with query parameters)")
    public Mono<Page<TransactionResponseDto>> findPayOutAll(@RequestHeader(name = "Authorization") String auth, Pageable pageable,
            @RequestParam(required = false, name = "start_date") LocalDateTime startDate,
            @RequestParam(required = false, name = "end_date") LocalDateTime endDate) {
        return super.findTransactionAll(auth, pageable, startDate, endDate, TransactionType.PAY_OUT);
    }

    @PostMapping("/payout")
    @Operation(tags = "createPayOut", description = "Create pay out top up",
            summary = "Transaction method allows to create payout (withdrawal) transaction and send money to customer payment card")
    public Mono<ResponseEntity<TransactionCreateResponseDto>> createPayOut(
            @RequestHeader("Authorization") String auth, @Valid @RequestBody TransactionRequestDto requestDto) {
        return super.createTransaction(auth, requestDto, TransactionType.PAY_OUT);
    }
}