package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.*;
import com.invox.invoice_system.entity.Customer;
import com.invox.invoice_system.repository.CustomerRepository;
import com.invox.invoice_system.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        Customer customer = Customer.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .totalPoints(dto.getTotalPoints())
                .availablePoints(dto.getAvailablePoints())
                .build();
        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setTotalPoints(dto.getTotalPoints());
        customer.setAvailablePoints(dto.getAvailablePoints());
        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    private CustomerResponseDTO mapToDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getTotalPoints(),
                customer.getAvailablePoints()
        );
    }
}
