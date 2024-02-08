package com.askfar.fakepaymentprovider.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "webhooks_history")
public class WebhookHistory {

    @Id
    private Long id;

    private String notificationUrl;

    private String request;

    private String response;

    private LocalDateTime createdAt = LocalDateTime.now();
}