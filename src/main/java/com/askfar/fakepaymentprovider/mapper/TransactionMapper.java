package com.askfar.fakepaymentprovider.mapper;

import com.askfar.fakepaymentprovider.dto.TransactionTopUpRequestDto;
import com.askfar.fakepaymentprovider.dto.TransactionTopUpResponseDto;
import com.askfar.fakepaymentprovider.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionTopUpRequestDto toMapTopUpRequestDto(Transaction entity);

    TransactionTopUpResponseDto toMapTopUpResponseDto(Transaction entity);

    Transaction toMapTransactionEntity(TransactionTopUpRequestDto dto);
}