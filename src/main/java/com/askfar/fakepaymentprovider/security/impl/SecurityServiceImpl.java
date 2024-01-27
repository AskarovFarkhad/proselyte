package com.askfar.fakepaymentprovider.security.impl;

import com.askfar.fakepaymentprovider.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    @Override
    public boolean authorization(String authorization) {
        return true;
    }
}
