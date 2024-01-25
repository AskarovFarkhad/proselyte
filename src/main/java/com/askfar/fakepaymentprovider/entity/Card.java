package com.askfar.fakepaymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "cards")
public class Card {

    @Id
    private Long cardId;

    private String cardNumber;

    private Date expDate;

    private int cvv;

    private Long customerId;
}
