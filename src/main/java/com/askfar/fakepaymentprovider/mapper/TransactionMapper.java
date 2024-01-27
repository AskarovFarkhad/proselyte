package com.askfar.fakepaymentprovider.mapper;

import com.askfar.fakepaymentprovider.dto.TransactionTopUpRequestDto;
import com.askfar.fakepaymentprovider.dto.TransactionTopUpResponseDto;
import com.askfar.fakepaymentprovider.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "cardData", source = "card")
    TransactionTopUpRequestDto toMapTopUpRequestDto(Transaction entity);

    @Mapping(target = "cardData", source = "card")
    TransactionTopUpResponseDto toMapTopUpResponseDto(Transaction entity);
}