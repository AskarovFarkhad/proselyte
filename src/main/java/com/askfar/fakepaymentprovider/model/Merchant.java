package com.askfar.fakepaymentprovider.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "merchants")
public class Merchant {

    @Id
    private String merchantId;

    private String secretKey;

    private boolean enabled;
}

