package com.askfar.fakepaymentprovider.security;

import reactor.core.publisher.Mono;

public interface SecurityService {

    Mono<String> authorization(String authorization);
}
