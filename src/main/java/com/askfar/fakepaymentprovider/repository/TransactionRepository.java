package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Repository
@EnableR2dbcRepositories
public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {

    @Query("""
            SELECT * FROM transactions
            WHERE transaction_type = 'TOP_UP' AND created_at = current_date AND (status = 'FAILED' OR status = 'SUCCESS')
            ORDER BY created_at
            """)
    Flux<Transaction> findAllByToday(Pageable pageable);

    @Query("""
            SELECT count(*) FROM transactions
            WHERE transaction_type = 'TOP_UP' AND created_at = current_date AND (status = 'FAILED' OR status = 'SUCCESS')
            """)
    Mono<Integer> countAllByToday();

    @Query("""
            SELECT * FROM transactions
            WHERE transaction_type = 'TOP_UP' AND created_at >= :startDate AND created_at <= :endDate AND (status = 'FAILED' OR status = 'SUCCESS')
            ORDER BY created_at
            """)
    Flux<Transaction> findAllByQueryDate(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("""
            SELECT count(*) FROM transactions
            WHERE transaction_type = 'TOP_UP' AND created_at >= :startDate AND created_at <= :endDate AND (status = 'FAILED' OR status = 'SUCCESS')
            """)
    Mono<Integer> countAllByQueryDate(LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT * FROM transactions
            WHERE transaction_id = :transactionId AND (status = 'FAILED' OR status = 'SUCCESS')
            """)
    Mono<Transaction> findByTransactionId(UUID transactionId);
}
