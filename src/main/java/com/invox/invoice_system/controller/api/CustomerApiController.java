package com.invox.invoice_system.controller.api;

import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import com.invox.invoice_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequestDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, customerRequestDTO);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search") // Ví dụ: /api/customers/search?term=david
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(@RequestParam("term") String searchTerm) {
        List<CustomerResponseDTO> customers = customerService.searchCustomersByNameOrPhone(searchTerm);
        return ResponseEntity.ok(customers);
    }
}