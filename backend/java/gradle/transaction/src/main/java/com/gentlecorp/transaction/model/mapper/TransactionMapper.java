package com.gentlecorp.transaction.model.mapper;

import com.gentlecorp.transaction.model.dto.TransactionDTO;
import com.gentlecorp.transaction.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
  Transaction toTransaction(TransactionDTO transactionDTO);

  TransactionDTO toDTO(Transaction transaction);
}
