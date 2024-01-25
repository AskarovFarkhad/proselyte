package com.askfar.fakepaymentprovider.entity;

import com.askfar.fakepaymentprovider.enums.PaymentMethod;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "transactions")
public class Transaction {

    @Id
    private Long id;

    private UUID transactionId;

    private TransactionType transactionType;

    private PaymentMethod paymentMethod;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String language;

    private String notificationUrl;

    private Customer customer;

    private Card cardData;

    private String status;

    private String message;
}
