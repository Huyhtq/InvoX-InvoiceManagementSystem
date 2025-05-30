package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import com.invox.invoice_system.entity.Customer;
import com.invox.invoice_system.entity.MemberRank;
import com.invox.invoice_system.mapper.CustomerMapper;
import com.invox.invoice_system.mapper.MemberRankMapper;
import com.invox.invoice_system.repository.CustomerRepository;
import com.invox.invoice_system.service.CustomerService;
import com.invox.invoice_system.service.MemberRankService; // Để gọi MemberRankService
import com.invox.invoice_system.service.PointTransactionService; // Để gọi PointTransactionService
import com.invox.invoice_system.enums.PointTransactionType; // Import enum
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final MemberRankMapper memberRankMapper;
    private final MemberRankService memberRankService; // Inject MemberRankService
    private final PointTransactionService pointTransactionService; // Inject PointTransactionService

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponseDTO> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponseDto);
    }

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        // Kiểm tra số điện thoại và email duy nhất
        if (customerRequestDTO.getPhone() != null && customerRepository.findByPhone(customerRequestDTO.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + customerRequestDTO.getPhone());
        }
        if (customerRequestDTO.getEmail() != null && customerRepository.findByEmail(customerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại: " + customerRequestDTO.getEmail());
        }

        Customer customer = customerMapper.toEntity(customerRequestDTO);

        // Gán hạng thành viên mặc định (ví dụ: ID = 1 là hạng Đồng)
        // Bạn có thể tìm hạng mặc định bằng tên hoặc qua một cấu hình nào đó
        MemberRank defaultRank = memberRankService.getMemberRankById(1L) // Giả sử ID 1 là hạng Đồng
                .map(memberRankMapper::toEntity) // Chuyển DTO sang Entity nếu cần
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy hạng thành viên mặc định (ID 1)."));
        customer.setMemberRank(defaultRank);
        customer.setTotalPoints(0L);
        customer.setAvailablePoints(0L);

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + id));

        // Kiểm tra số điện thoại và email duy nhất khi cập nhật
        if (customerRequestDTO.getPhone() != null) {
            Optional<Customer> customerWithPhone = customerRepository.findByPhone(customerRequestDTO.getPhone());
            if (customerWithPhone.isPresent() && !customerWithPhone.get().getId().equals(id)) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + customerRequestDTO.getPhone());
            }
        }
        if (customerRequestDTO.getEmail() != null) {
            Optional<Customer> customerWithEmail = customerRepository.findByEmail(customerRequestDTO.getEmail());
            if (customerWithEmail.isPresent() && !customerWithEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email đã tồn tại: " + customerRequestDTO.getEmail());
            }
        }

        // Cập nhật các trường từ DTO vào Entity
        existingCustomer.setName(customerRequestDTO.getName());
        existingCustomer.setPhone(customerRequestDTO.getPhone());
        existingCustomer.setEmail(customerRequestDTO.getEmail());
        existingCustomer.setAddress(customerRequestDTO.getAddress());
        existingCustomer.setBirthDate(customerRequestDTO.getBirthDate());
        existingCustomer.setGender(customerRequestDTO.getGender());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toResponseDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + id);
        }
        // Thêm logic kiểm tra nếu có hóa đơn hoặc giao dịch điểm liên quan
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CustomerResponseDTO addPointsToCustomer(Long customerId, Long pointsToAdd) {
        if (pointsToAdd <= 0) {
            throw new IllegalArgumentException("Số điểm thêm phải lớn hơn 0.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));

        customer.setTotalPoints(customer.getTotalPoints() + pointsToAdd);
        customer.setAvailablePoints(customer.getAvailablePoints() + pointsToAdd);

        // Cập nhật hạng thành viên nếu đủ điểm
        MemberRank newRank = memberRankService.getMemberRankForPoints(customer.getTotalPoints());
        if (newRank != null && !newRank.getId().equals(customer.getMemberRank().getId())) {
            customer.setMemberRank(newRank);
        }

        Customer updatedCustomer = customerRepository.save(customer);

        // Ghi log giao dịch điểm
        pointTransactionService.createPointTransaction(
            customer,
            null, // Không liên quan đến hóa đơn cụ thể ở đây
            PointTransactionType.EARN,
            pointsToAdd,
            "Tích điểm thủ công/từ sự kiện"
        );

        return customerMapper.toResponseDto(updatedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO redeemPointsFromCustomer(Long customerId, Long pointsToRedeem) {
        if (pointsToRedeem <= 0) {
            throw new IllegalArgumentException("Số điểm đổi phải lớn hơn 0.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));

        if (customer.getAvailablePoints() < pointsToRedeem) {
            throw new IllegalArgumentException("Số điểm khả dụng không đủ để đổi.");
        }

        customer.setAvailablePoints(customer.getAvailablePoints() - pointsToRedeem);
        Customer updatedCustomer = customerRepository.save(customer);

        // Ghi log giao dịch điểm
        pointTransactionService.createPointTransaction(
            customer,
            null, // Có thể liên quan đến hóa đơn cụ thể nếu là giảm giá hóa đơn
            PointTransactionType.REDEEM,
            -pointsToRedeem, // Điểm bị trừ là số âm
            "Đổi điểm thủ công/ưu đãi"
        );

        return customerMapper.toResponseDto(updatedCustomer);
    }
}