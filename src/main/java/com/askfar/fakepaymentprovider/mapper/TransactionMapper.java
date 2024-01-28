package com.askfar.fakepaymentprovider.mapper;

import com.askfar.fakepaymentprovider.dto.request.TransactionRequestDto;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.model.Transaction;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "cardData", source = "card")
    TransactionResponseDto toMapResponseDto(Transaction entity);

    @InheritInverseConfiguration
    @Mapping(target = "card", source = "cardData")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "transactionType", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "cardId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "card.cardId", ignore = true)
    @Mapping(target = "card.customer", ignore = true)
    @Mapping(target = "card.customerId", ignore = true)
    @Mapping(target = "customer.customerId", ignore = true)
    Transaction toTransactionEntity(TransactionRequestDto requestDto);
}