package com.webbazar.controller;

import com.webbazar.dto.admin.AdminCreateUserRequestDTO;
import com.webbazar.dto.admin.AdminUpdateUserRequestDTO;
import com.webbazar.dto.admin.AdminUpdateUserEnabledRequestDTO;
import com.webbazar.dto.admin.AdminUserDetailDTO;
import com.webbazar.dto.admin.AdminUserListDTO;
import com.webbazar.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<AdminUserListDTO> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public AdminUserDetailDTO getUserById(@PathVariable Long id) {
        return adminUserService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminUserDetailDTO createUser(@Valid @RequestBody AdminCreateUserRequestDTO request) {
        return adminUserService.createUser(request);
    }

    @PutMapping("/{id}")
    public AdminUserDetailDTO updateUser(@PathVariable Long id,
                                         @Valid @RequestBody AdminUpdateUserRequestDTO request) {
        return adminUserService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }



    @PatchMapping("/{id}/enabled")
    public AdminUserDetailDTO updateUserEnabled(@PathVariable Long id,
                                                @Valid @RequestBody AdminUpdateUserEnabledRequestDTO request,
                                                Authentication authentication) {

        //  ingelogde admin
        String currentEmail = authentication.getName();

        // gebruiker ophalen
        AdminUserDetailDTO targetUser = adminUserService.getUserById(id);

        // Admin mag niet zichzelf blokkeren/deblokkeren
        if (targetUser.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Je kunt je eigen account niet blokkeren of deblokkeren."
            );
        }

        return adminUserService.updateUserEnabled(id, request.getEnabled());
    }
}
