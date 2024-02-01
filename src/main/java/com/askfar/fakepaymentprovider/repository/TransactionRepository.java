package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.askfar.fakepaymentprovider.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {

    @Query("""
            SELECT * FROM transactions
            WHERE transaction_type = :transactionType AND date(created_at) = current_date AND (status = 'FAILED' OR status = 'SUCCESS')
            ORDER BY created_at DESC
            """)
    Flux<Transaction> findAllByToday(Pageable pageable, TransactionType transactionType);

    @Query("""
            SELECT count(*) FROM transactions
            WHERE transaction_type = :transactionType AND date(created_at) = current_date AND (status = 'FAILED' OR status = 'SUCCESS')
            """)
    Mono<Integer> countAllByToday(TransactionType transactionType);

    @Query("""
            SELECT * FROM transactions
            WHERE transaction_type = :transactionType AND created_at >= :startDate AND created_at <= :endDate AND (status = 'FAILED' OR status = 'SUCCESS')
            ORDER BY created_at
            """)
    Flux<Transaction> findAllByQueryDate(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, TransactionType transactionType);

    @Query("""
            SELECT count(*) FROM transactions
            WHERE transaction_type = :transactionType AND created_at >= :startDate AND created_at <= :endDate AND (status = 'FAILED' OR status = 'SUCCESS')
            """)
    Mono<Integer> countAllByQueryDate(LocalDateTime startDate, LocalDateTime endDate, TransactionType transactionType);

    @Query("""
            SELECT * FROM transactions
            WHERE transaction_id = :transactionId AND (status = 'FAILED' OR status = 'SUCCESS')
            """)
    Mono<Transaction> findByTransactionId(UUID transactionId);

    Flux<Transaction> findTransactionByTransactionTypeAndStatus(TransactionType type, TransactionStatus status);

    @Query("UPDATE transactions SET status = :status, message = :message, updated_at = :updatedAt WHERE id = :id")
    Mono<Transaction> updateTransaction(Long id, TransactionStatus status, String message, LocalDateTime updatedAt);
}
