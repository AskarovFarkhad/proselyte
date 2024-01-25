package com.askfar.fakepaymentprovider.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "merchants")
public class Merchant {

    @Id
    private String merchantId;

    private String secretKey;

    private boolean enabled;

    @ToString.Include(name = "secretKey")
    private String maskSecretKey() {
        return "********";
    }
}

