package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Customer;
import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component // Đánh dấu là Spring Component để có thể @Autowired
@RequiredArgsConstructor // Nếu bạn có các dependency khác cần inject vào mapper này
public class CustomerMapper {

    // Cần inject MemberRankMapper để ánh xạ MemberRank Entity sang MemberRankDTO
    private final MemberRankMapper memberRankMapper;

    // Convert Customer Entity to CustomerResponseDTO
    public CustomerResponseDTO toResponseDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setBirthDate(customer.getBirthDate());
        dto.setGender(customer.getGender()); // Enum Gender
        dto.setTotalPoints(customer.getTotalPoints());
        dto.setAvailablePoints(customer.getAvailablePoints());

        // Ánh xạ MemberRank Entity sang MemberRankDTO
        if (customer.getMemberRank() != null) {
            dto.setMemberRank(memberRankMapper.toDto(customer.getMemberRank())); // Gọi memberRankMapper
        }

        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }

    // Convert CustomerRequestDTO to Customer Entity (khi tạo mới hoặc cập nhật)
    // Lưu ý: Các trường totalPoints, availablePoints, memberRank sẽ được Service quản lý/gán
    public Customer toEntity(CustomerRequestDTO customerRequestDTO) {
        if (customerRequestDTO == null) {
            return null;
        }

        Customer entity = new Customer();
        entity.setId(customerRequestDTO.getId());
        entity.setName(customerRequestDTO.getName());
        entity.setPhone(customerRequestDTO.getPhone());
        entity.setEmail(customerRequestDTO.getEmail());
        entity.setAddress(customerRequestDTO.getAddress());
        entity.setBirthDate(customerRequestDTO.getBirthDate());
        entity.setGender(customerRequestDTO.getGender()); // Enum Gender

        return entity;
    }

    // Phương thức này có thể hữu ích khi cập nhật một Customer hiện có từ DTO
    public void updateEntityFromDto(CustomerRequestDTO dto, Customer entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setBirthDate(dto.getBirthDate());
        entity.setGender(dto.getGender());
        // Các trường điểm và hạng thành viên thường được Service xử lý riêng
    }
}