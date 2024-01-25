package com.askfar.fakepaymentprovider.mapper;

import com.askfar.fakepaymentprovider.dto.MerchantDto;
import com.askfar.fakepaymentprovider.entity.Merchant;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    MerchantDto mapToDto(Merchant entity);

    @InheritInverseConfiguration
    Merchant mapToEntity(MerchantDto dto);
}
