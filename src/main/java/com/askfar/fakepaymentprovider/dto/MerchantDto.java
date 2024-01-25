package com.askfar.fakepaymentprovider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantDto {

    private String merchantId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String secretKey;

    private boolean enabled;
}
