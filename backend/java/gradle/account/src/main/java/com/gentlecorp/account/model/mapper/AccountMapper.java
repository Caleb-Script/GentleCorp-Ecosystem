package com.gentlecorp.account.model.mapper;

import com.gentlecorp.account.model.dto.AccountDTO;
import com.gentlecorp.account.model.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
  Account toAccount(AccountDTO accountDTO);

  AccountDTO toDTO(Account account);
}
