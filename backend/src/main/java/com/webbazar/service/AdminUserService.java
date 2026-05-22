package com.webbazar.service;

import com.webbazar.dto.admin.AdminCreateUserRequestDTO;
import com.webbazar.dto.admin.AdminUpdateUserRequestDTO;
import com.webbazar.dto.admin.AdminUserDetailDTO;
import com.webbazar.dto.admin.AdminUserListDTO;

import java.util.List;

public interface AdminUserService {

    List<AdminUserListDTO> getAllUsers();

    AdminUserDetailDTO getUserById(Long id);

    AdminUserDetailDTO createUser(AdminCreateUserRequestDTO request);

    AdminUserDetailDTO updateUser(Long id, AdminUpdateUserRequestDTO request);

    void deleteUser(Long id);


    AdminUserDetailDTO updateUserEnabled(Long id, Boolean enabled);
}
