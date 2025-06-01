package com.invox.invoice_system.controller.api;

import com.invox.invoice_system.dto.UserRegistrationDTO;
import com.invox.invoice_system.dto.UserResponseDTO;
import com.invox.invoice_system.dto.LoginRequestDTO; // For login API
import com.invox.invoice_system.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserApiController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager; // Cần cho API đăng nhập (nếu không dùng form login mặc định)

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = appUserService.getAllAppUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return appUserService.getAppUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/register") // API đăng ký người dùng mới
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO registeredUser = appUserService.registerNewUser(userRegistrationDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}") // API cập nhật thông tin người dùng
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO updatedUser = appUserService.updateAppUser(id, userRegistrationDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteAppUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // API để đổi mật khẩu (chỉ cần gửi mật khẩu mới)
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        // Trong thực tế, bạn nên dùng một DTO cho việc đổi mật khẩu (chứa oldPassword, newPassword)
        // và xác thực người dùng hiện tại có phải là người đang đổi mật khẩu không
        appUserService.changePassword(id, newPassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // --- API Đăng nhập (nếu không dùng form login của Spring Security) ---
    // Phương thức này có thể được xử lý bởi Spring Security tự động qua form login
    // Nếu bạn muốn API login thủ công cho AJAX, bạn có thể dùng cái này
    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Trong môi trường session, Spring Security sẽ tự động tạo session và cookie JSESSIONID
        // Bạn có thể trả về một thông báo thành công hoặc thông tin user cơ bản
        return new ResponseEntity<>("Đăng nhập thành công!", HttpStatus.OK);
    }
}