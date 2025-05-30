package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.entity.Customer; // Import for mapping ID
import com.invox.invoice_system.entity.Employee; // Import for mapping ID
import com.invox.invoice_system.enums.PaymentMethod; // Import enum
import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, EmployeeMapper.class, InvoiceDetailMapper.class})
public interface InvoiceMapper {

    InvoiceMapper INSTANCE = Mappers.getMapper(InvoiceMapper.class);

    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "employee", target = "employee")
    @Mapping(source = "invoiceDetails", target = "invoiceDetails")
    InvoiceResponseDTO toResponseDto(Invoice invoice);

    // Mapping for Invoice creation request
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "employeeId", target = "employee.id")
    @Mapping(source = "paymentMethod", target = "paymentMethod") // Direct mapping for Enum
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceDate", ignore = true) // Set by service
    @Mapping(target = "totalAmount", ignore = true) // Calculated by service
    @Mapping(target = "discountAmount", ignore = true) // Handled by service
    @Mapping(target = "pointsRedeemed", ignore = true) // Handled by service
    @Mapping(target = "finalAmount", ignore = true) // Calculated by service
    @Mapping(target = "status", ignore = true) // Set by service
    @Mapping(target = "updatedAt", ignore = true) // Handled by @UpdateTimestamp
    @Mapping(target = "pointTransactions", ignore = true) // Handled by service
    @Mapping(source = "items", target = "invoiceDetails") // Map list of DTOs to list of Entities
    Invoice toEntity(InvoiceCreationRequestDTO requestDTO);


    // Helper for mapping Long ID to Customer entity
    default Customer toCustomerEntity(Long customerId) {
        if (customerId == null) return null;
        Customer customer = new Customer();
        customer.setId(customerId);
        return customer;
    }

    // Helper for mapping Long ID to Employee entity
    default Employee toEmployeeEntity(Long employeeId) {
        if (employeeId == null) return null;
        Employee employee = new Employee();
        employee.setId(employeeId);
        return employee;
    }

    // Helper for mapping String to PaymentMethod enum
    @Named("mapPaymentMethod")
    default PaymentMethod mapPaymentMethod(String method) {
        if (method == null) {
            return null;
        }
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle invalid payment method string, perhaps log or throw custom exception
            return null; // Or a default value
        }
    }

    // Helper for mapping PaymentMethod enum to String
    @Named("mapPaymentMethodToString")
    default String mapPaymentMethodToString(PaymentMethod method) {
        if (method == null) {
            return null;
        }
        return method.name();
    }
}