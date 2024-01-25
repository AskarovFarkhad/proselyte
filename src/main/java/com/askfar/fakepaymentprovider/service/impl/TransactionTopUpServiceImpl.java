package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.dto.TransactionTopUpResponseDto;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.service.TransactionTopUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionTopUpServiceImpl implements TransactionTopUpService {

    private final TransactionMapper transactionMapper;

    private final TransactionRepository transactionRepository;

    @Override
    public Mono<TransactionTopUpResponseDto> findTransactionDetails(@PathVariable UUID transactionId) {
        // TODO implement security
        return transactionRepository.findByTransactionId(transactionId)
                                    .map(transaction -> transaction.orElseThrow(RuntimeException::new))
                                    .log()
                                    .map(transactionMapper::toMapTopUpResponseDto);
    }

    @Override
    public Mono<Page<TransactionTopUpResponseDto>> findTransactionAll(Pageable pageable) {
        // TODO implement security
        return this.transactionRepository.findAllByToday(pageable)
                                         .log()
                                         .map(transactionMapper::toMapTopUpResponseDto)
                                         .collectList()
                                         .zipWith(this.transactionRepository.countAllByToday())
                                         .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    public Mono<Page<TransactionTopUpResponseDto>> findTransactionAll(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // TODO implement security
        return this.transactionRepository.findAllByQueryDate(startDate, endDate, pageable)
                                         .log()
                                         .map(transactionMapper::toMapTopUpResponseDto)
                                         .collectList()
                                         .zipWith(this.transactionRepository.countAllByQueryDate(startDate, endDate))
                                         .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }
}