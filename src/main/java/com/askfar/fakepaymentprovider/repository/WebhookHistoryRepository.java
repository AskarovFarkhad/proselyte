package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.model.WebhookHistory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookHistoryRepository extends R2dbcRepository<WebhookHistory, Long> {
}