package com.askfar.fakepaymentprovider.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "wallets")
public class Wallet {

    @Id
    private Long walletId;

    private String currency;

    private BigDecimal balance;

    private String merchantId;

    public void addAmount(BigDecimal amount) {
        this.balance = balance.add(amount);
    }
}
