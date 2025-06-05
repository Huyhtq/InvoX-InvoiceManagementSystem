package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import com.invox.invoice_system.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerResponseDTO> getAllCustomers();
    Optional<CustomerResponseDTO> getCustomerById(Long id);
    CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO);
    void deleteCustomer(Long id);
    CustomerResponseDTO addPointsToCustomer(Long customerId, Long pointsToAdd);
    CustomerResponseDTO redeemPointsFromCustomer(Long customerId, Long pointsToRedeem);
    List<CustomerResponseDTO> searchCustomersByTerm(String searchTerm);
    CustomerResponseDTO redeemPointsFromCustomer(Long customerId, Long pointsToRedeem, Invoice invoice);
    void updateCustomerPointsAfterEarning(Long customerId, Long pointsEarned);
    List<CustomerResponseDTO> searchCustomersByNameOrPhone(String searchTerm);
}