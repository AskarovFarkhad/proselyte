package com.askfar.fakepaymentprovider.service.impl;

import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.dto.request.TransactionTopUpRequestDto;
import com.askfar.fakepaymentprovider.enums.TransactionMessage;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.exception.BusinessException;
import com.askfar.fakepaymentprovider.exception.NotFoundException;
import com.askfar.fakepaymentprovider.mapper.TransactionMapper;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.repository.CardRepository;
import com.askfar.fakepaymentprovider.repository.CustomerRepository;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.repository.WalletRepository;
import com.askfar.fakepaymentprovider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CardRepository cardRepository;

    private final TransactionMapper transactionMapper;

    private final WalletRepository walletRepository;

    private final CustomerRepository customerRepository;

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<TransactionResponseDto> findTransactionDetails(@PathVariable UUID transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                                    .flatMap(this::loadRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transaction by transactionId = %s not found", transactionId))));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<TransactionResponseDto>> findTransactionAll(Pageable pageable) {
        return transactionRepository.findAllByToday(pageable, TransactionType.TOP_UP)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions by %s not found", LocalDate.now()))))
                                    .flatMap(this::loadRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByToday(TransactionType.TOP_UP))
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<TransactionResponseDto>> findTransactionAll(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return transactionRepository.findAllByQueryDate(startDate, endDate, pageable, TransactionType.TOP_UP)
                                    .switchIfEmpty(Mono.error(new NotFoundException(format("Transactions from %s to %s not found", startDate, endDate))))
                                    .flatMap(this::loadRelations)
                                    .map(transactionMapper::toMapResponseDto)
                                    .collectList()
                                    .zipWith(transactionRepository.countAllByQueryDate(startDate, endDate, TransactionType.TOP_UP))
                                    .map(page -> new PageImpl<>(page.getT1(), pageable, page.getT2()));
    }

    @Override
    @Transactional
    public Mono<Transaction> createTransaction(TransactionTopUpRequestDto requestDto, String merchantId) {
        if (walletRepository.isExistsWalletOfCurrency(merchantId, requestDto.getCurrency().name())) {
            Transaction transaction = transactionMapper.toTransactionEntity(requestDto);
            enrichTransaction(transaction);

            // бизнес логика, в нашем случае после успешной валидации берем в работу и сохраняем в хранилище

            walletRepository.updateBalanceByMerchantIdAndCurrency(merchantId, transaction.getCurrency(), transaction.getAmount());

            return Mono.just(transaction)
                       .flatMap(t -> customerRepository.save(t.getCustomer()))
                       .map(customer -> transaction.setCustomerId(customer.getCustomerId()))
                       .flatMap(t -> cardRepository.save(t.getCard()))
                       .map(card -> transaction.setCardId(card.getCardId()))
                       .flatMap(transactionRepository::save);
        }
        throw new BusinessException(format("There is no wallet with this currency (%s)", requestDto.getCurrency()));
    }

    private void enrichTransaction(Transaction transaction) {
        transaction.setTransactionId(UUID.randomUUID())
                   .setTransactionType(TransactionType.TOP_UP)
                   .setStatus(TransactionStatus.IN_PROGRESS)
                   .setMessage(TransactionMessage.PROGRESS.getMsg())
                   .setUpdatedAt(LocalDateTime.now());
    }

    private Mono<Transaction> loadRelations(final Transaction transaction) {
        Mono<Transaction> transactionMono = Mono.just(transaction)
                                                .zipWith(customerRepository.findById(transaction.getCustomerId()))
                                                .map(result -> result.getT1().setCustomer(result.getT2()));

        return transactionMono.zipWith(cardRepository.findById(transaction.getCardId()))
                              .map(result -> result.getT1().setCard(result.getT2()));
    }
}
