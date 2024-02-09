package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.model.Card;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CardRepository extends R2dbcRepository<Card, Long> {

    Mono<Card> findByCardNumber(String cardNumber);
}
