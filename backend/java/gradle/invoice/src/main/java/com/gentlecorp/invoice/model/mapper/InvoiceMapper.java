package com.gentlecorp.invoice.model.mapper;

import com.gentlecorp.invoice.model.dto.InvoiceDTO;
import com.gentlecorp.invoice.model.dto.PaymentDTO;
import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvoiceMapper {
  Invoice toInvoice(InvoiceDTO invoiceDTO);

  InvoiceDTO toDTO(Invoice invoice);

  Payment toPayment(PaymentDTO paymentDTO);
  PaymentDTO toDTO(Payment payment);

}
