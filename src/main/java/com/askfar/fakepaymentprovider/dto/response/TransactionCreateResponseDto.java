package com.askfar.fakepaymentprovider.dto.response;

import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionCreateResponseDto {

    private UUID transactionId;

    private TransactionStatus status;

    private String message;
}