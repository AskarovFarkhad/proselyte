package com.askfar.fakepaymentprovider.security.impl;

import com.askfar.fakepaymentprovider.repository.MerchantRepository;
import com.askfar.fakepaymentprovider.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final MerchantRepository merchantRepository;

    private static final String ERROR_MSG = "Incorrect login information";

    @Override
    public String authorization(String authorization) {
        if (!authorization.startsWith("Basic ")) {
            log.error(ERROR_MSG);
            throw new SecurityException(ERROR_MSG);
        }

        String merchantId;
        String secretKey;
        try {
            String encodedCredentials = authorization.substring(6);
            String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials), StandardCharsets.UTF_8);
            String[] credentials = decodedCredentials.split(":");
            merchantId = credentials[0].split("=")[1];
            secretKey = credentials[1].split("=")[1];
        } catch (Exception e) {
            log.error(ERROR_MSG, e);
            throw new SecurityException(ERROR_MSG);
        }

        merchantRepository.findByMerchantIdAndSecretKeyAndEnabled(merchantId, secretKey, true)
                          .switchIfEmpty(Mono.error(new SecurityException("Authorization failed")))
                          .block();

        log.info("Authorization successfully");
        return merchantId;
    }
}
