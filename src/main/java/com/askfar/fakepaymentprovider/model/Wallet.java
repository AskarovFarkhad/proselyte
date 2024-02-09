package com.askfar.fakepaymentprovider.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
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

    public void subtractAmount(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }

    public boolean hasBalanceDepth(BigDecimal amount) {
        return balance.subtract(amount).compareTo(BigDecimal.ZERO) != -1;
    }
}
