package com.askfar.fakepaymentprovider.model;

import com.askfar.fakepaymentprovider.enums.CurrencyEnum;
import com.askfar.fakepaymentprovider.enums.PaymentMethod;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "transactions")
public class Transaction {

    @Id
    private Long id;

    private UUID transactionId;

    private TransactionType transactionType;

    private PaymentMethod paymentMethod;

    private BigDecimal amount;

    private CurrencyEnum currency;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String language;

    private String notificationUrl;

    private Long customerId;

    @Transient
    private Customer customer;

    private Long cardId;

    @Transient
    private Card card;

    private TransactionStatus status;

    private String message;
}
