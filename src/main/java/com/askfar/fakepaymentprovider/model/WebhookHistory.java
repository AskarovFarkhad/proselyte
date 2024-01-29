package com.askfar.fakepaymentprovider.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "cards")
public class WebhookHistory {

    @Id
    private Long cardId;

    private String notificationUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String response;

    private UUID transactionId;
}