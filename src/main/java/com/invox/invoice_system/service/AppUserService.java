package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.*;
import java.util.*;

public interface AppUserService {
    AppUserResponseDTO createUser(AppUserRequestDTO dto);
    AppUserResponseDTO getUserById(Long id);
    List<AppUserResponseDTO> getAllUsers();
    void deleteUser(Long id);
    AppUserResponseDTO updateUser(Long id, AppUserRequestDTO dto);
    AppUserResponseDTO login(AppUserLoginDTO dto);
}
