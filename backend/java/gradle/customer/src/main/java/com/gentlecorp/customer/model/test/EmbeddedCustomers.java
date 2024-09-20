package com.gentlecorp.customer.model.test;

import com.gentlecorp.customer.model.entity.Customer;
import lombok.Getter;

import java.util.List;

@Getter
public class EmbeddedCustomers {
  private List<Customer> customers;
}
