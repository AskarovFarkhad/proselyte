package com.askfar.fakepaymentprovider.dto.request;

import com.askfar.fakepaymentprovider.config.LocalDateDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@Validated
@Builder(toBuilder = true)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CardRequestDto {

    @Size(min = 16, max = 16)
    private String cardNumber;

    @NotNull
    @FutureOrPresent
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate expDate;

    @Pattern(regexp = "\\d{3}")
    private int cvv;
}
