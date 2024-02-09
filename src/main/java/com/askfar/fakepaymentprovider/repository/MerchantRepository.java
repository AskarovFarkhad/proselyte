package com.askfar.fakepaymentprovider.repository;

import com.askfar.fakepaymentprovider.model.Merchant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MerchantRepository extends R2dbcRepository<Merchant, Long> {

    Mono<Merchant> findByMerchantIdAndSecretKeyAndEnabled(String merchantId, String secretKey, boolean enabled);
}
