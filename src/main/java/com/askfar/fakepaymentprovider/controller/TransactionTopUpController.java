package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.dto.TransactionTopUpResponseDto;
import com.askfar.fakepaymentprovider.service.impl.TransactionTopUpServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${rest.api}")
public class TransactionTopUpController {

    private final TransactionTopUpServiceImpl topUpService;

    @GetMapping("/transaction/{transactionId}/details")
    public Mono<TransactionTopUpResponseDto> findTransactionDetails(@PathVariable UUID transactionId) {
        return topUpService.findTransactionDetails(transactionId);
    }

    @GetMapping("/transaction/list")
    public Mono<Page<TransactionTopUpResponseDto>> findTransactionAll(Pageable pageable) {
        return topUpService.findTransactionAll(pageable);
    }

    @GetMapping("/transaction/list")
    public Mono<Page<TransactionTopUpResponseDto>> findTransactionAll(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, Pageable pageable) {
        return topUpService.findTransactionAll(startDate, endDate, pageable);
    }

    // TODO implement POST method
}
