package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.exception.NotFoundException;
import com.askfar.fakepaymentprovider.manager.TransactionManager;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.repository.CardRepository;
import com.askfar.fakepaymentprovider.repository.CustomerRepository;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionManager manager;

    private final CardRepository cardRepository;

    private final TransactionMapper transactionMapper;

    private final CustomerRepository customerRepository;

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<TransactionResponseDto> findTransactionDetails(UUID transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transaction by transactionId=%s not found", transactionId))))
                                    .doOnSuccess(dto -> log.info("Transaction with transactionId = {} found: {}", transactionId, dto));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<TransactionResponseDto>> findTransactionAll(Pageable pageable, TransactionType type) {
        return transactionRepository.findAllByToday(pageable, type)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions by %s not found", LocalDate.now()))))
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByToday(type))
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<TransactionResponseDto>> findTransactionAll(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, TransactionType type) {
        return transactionRepository.findAllByQueryDate(startDate, endDate, pageable, type)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions from %s to %s not found", startDate, endDate))))
                                    .flatMap(this::getRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByQueryDate(startDate, endDate, type))
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    @Transactional
    public Mono<Transaction> createTransaction(TransactionRequestDto requestDto, String merchantId, TransactionType type) {
        if (TransactionType.TOP_UP.equals(type)) {
            return manager.createTopUp(requestDto, merchantId);
        } else {
            return manager.createPayOut(requestDto, merchantId);
        }
    }

    private Mono<Transaction> getRelations(Transaction transaction) {
        return Mono.just(transaction)
                   .zipWith(customerRepository.findById(transaction.getCustomerId()))
                   .map(result -> result.getT1().setCustomer(result.getT2()))
                   .zipWith(cardRepository.findById(transaction.getCardId()))
                   .map(result -> result.getT1().setCard(result.getT2()));
    }
}