package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.model.Wallet;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface WalletRepository extends R2dbcRepository<Wallet, String> {

    @Query("SELECT count(*) > 0 FROM wallets WHERE merchant_id = :merchantId AND currency = :currency")
    boolean isExistsWalletOfCurrency(String merchantId, String currency);

    @Query("UPDATE wallets SET balance = (balance + :amount) WHERE merchant_id = :merchantId AND currency = :currency")
    void updateBalanceByMerchantIdAndCurrency(String merchantId, String currency, BigDecimal amount);
}
