package com.askfar.fakepaymentprovider.dto.request;

import com.askfar.fakepaymentprovider.dto.CustomerDto;
import com.askfar.fakepaymentprovider.enums.CurrencyEnum;
import com.askfar.fakepaymentprovider.enums.PaymentMethod;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionWebhookRequestDto {

    private UUID transactionId;

    private TransactionType transactionType;

    private PaymentMethod paymentMethod;

    private BigDecimal amount;

    private CurrencyEnum currency;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String language;

    private CardRequestDto card;

    private CustomerDto customer;

    private TransactionStatus status;

    private String message;
}