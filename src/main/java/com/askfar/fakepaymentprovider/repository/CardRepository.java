package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.entity.Card;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends R2dbcRepository<Card, Long> {
}
