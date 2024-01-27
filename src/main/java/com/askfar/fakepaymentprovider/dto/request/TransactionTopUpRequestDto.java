package com.askfar.fakepaymentprovider.dto.request;

import com.askfar.fakepaymentprovider.dto.CustomerDto;
import com.askfar.fakepaymentprovider.enums.CurrencyEnum;
import com.askfar.fakepaymentprovider.enums.PaymentMethod;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Validated
@Builder(toBuilder = true)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionTopUpRequestDto {

    private PaymentMethod paymentMethod;

    @PositiveOrZero
    private BigDecimal amount;

    private CurrencyEnum currency;

    @PastOrPresent
    private LocalDateTime createdAt;

    @PastOrPresent
    private LocalDateTime updatedAt;

    @Valid
    private CardRequestDto cardData;

    @NotBlank
    private String language;

    @NotBlank
    private String notificationUrl;

    @Valid
    private CustomerDto customer;
}