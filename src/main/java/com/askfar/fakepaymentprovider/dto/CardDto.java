package com.askfar.fakepaymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Data
@Validated
@Builder(toBuilder = true)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CardDto {

    @Size(min = 16, max = 16, message = "The card number must consist of 16 characters")
    private String cardNumber;

    private Date expDate;

    private int cvv;
}