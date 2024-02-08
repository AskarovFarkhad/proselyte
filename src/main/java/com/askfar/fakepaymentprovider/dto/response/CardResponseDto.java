package com.askfar.fakepaymentprovider.dto.response;

import com.askfar.fakepaymentprovider.config.jackson.CardNumberSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CardResponseDto {

    @Size(min = 16, max = 16, message = "The card number must consist of 16 characters")
    @JsonSerialize(using = CardNumberSerializer.class)
    private String cardNumber;
}
