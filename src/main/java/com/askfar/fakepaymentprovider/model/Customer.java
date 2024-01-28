package com.askfar.fakepaymentprovider.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "customers")
public class Customer {

    @Id
    private Long customerId;

    private String firstName;

    private String lastName;

    private String country;
}
