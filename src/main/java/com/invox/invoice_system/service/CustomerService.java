package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);
    CustomerResponseDTO getCustomerById(Long id);
    List<CustomerResponseDTO> getAllCustomers();
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);
    void deleteCustomer(Long id);
}
