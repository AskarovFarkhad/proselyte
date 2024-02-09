package com.askfar.fakepaymentprovider.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "cards")
public class Card {

    @Id
    private Long cardId;

    private String cardNumber;

    private LocalDate expDate;

    private int cvv;

    private Long customerId;

    @Transient
    private Customer customer;
}
