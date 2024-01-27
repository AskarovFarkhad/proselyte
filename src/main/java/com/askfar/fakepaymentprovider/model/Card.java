package com.askfar.fakepaymentprovider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "cards")
public class Card {

    @Id
    private Long cardId;

    private String cardNumber;

    private LocalDate expDate;

    private int cvv;

    @Transient
    private Customer customer;
}
