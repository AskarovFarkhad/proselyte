package com.askfar.fakepaymentprovider.model;

import com.askfar.fakepaymentprovider.enums.CurrencyEnum;
import com.askfar.fakepaymentprovider.enums.PaymentMethod;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.askfar.fakepaymentprovider.enums.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Table(name = "transactions")
public class Transaction implements Persistable<Long> {

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

    private String merchantId;

    private TransactionStatus status;

    private String message;

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
