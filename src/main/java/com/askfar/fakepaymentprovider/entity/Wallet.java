package com.askfar.fakepaymentprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "wallets")
public class Wallet {

    @Id
    private Long walletId;

    private String currency;

    private BigDecimal balance;

    private String merchantId;
}
