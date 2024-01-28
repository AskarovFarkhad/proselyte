package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.model.Wallet;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface WalletRepository extends R2dbcRepository<Wallet, Long> {

    Mono<Wallet> findByMerchantIdAndCurrency(String merchantId, String currency);

    @Query("UPDATE wallets SET balance = :balance WHERE wallet_id = :walletId")
    Mono<Wallet> updateWalletByWalletId(Long walletId, BigDecimal balance);
}
