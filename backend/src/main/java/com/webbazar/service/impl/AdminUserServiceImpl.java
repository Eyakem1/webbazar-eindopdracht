package com.webbazar.service.impl;

import com.webbazar.dto.admin.AdminCreateUserRequestDTO;
import com.webbazar.dto.admin.AdminUpdateUserRequestDTO;
import com.webbazar.dto.admin.AdminUserDetailDTO;
import com.webbazar.dto.admin.AdminUserListDTO;
import com.webbazar.entity.Role;
import com.webbazar.entity.User;
import com.webbazar.repo.RoleRepository;
import com.webbazar.repo.UserRepository;
import com.webbazar.service.AdminUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<AdminUserListDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toListDTO)
                .toList();
    }

    @Override
    public AdminUserDetailDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gebruiker niet gevonden"));
        return toDetailDTO(user);
    }

    @Override
    public AdminUserDetailDTO createUser(AdminCreateUserRequestDTO request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Gebruiker met email bestaat al: " + normalizedEmail);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER niet gevonden"));

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setEnabled(request.getEnabled() == null || request.getEnabled());
        user.setRoles(Set.of(userRole)); // alleen ROLE_USER

        User saved = userRepository.save(user);
        return toDetailDTO(saved);
    }

    @Override
    public AdminUserDetailDTO updateUser(Long id, AdminUpdateUserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gebruiker niet gevonden"));

        user.setName(request.getName());
        user.setAddress(request.getAddress());

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        // wachtwoord wijzigen als admin  invult
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);
        return toDetailDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            // zelfde stijl als ProductServiceImpl: not found → EntityNotFoundException
            throw new EntityNotFoundException("Gebruiker niet gevonden");
        }

        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            // Als er orders aan hangen en FK dit blokkeert
            throw new IllegalStateException(
                    "Gebruiker kan niet verwijderd worden omdat er nog gerelateerde data is."
            );
        }
    }

    // enabled-vlag wijzigen

    @Override
    public AdminUserDetailDTO updateUserEnabled(Long id, Boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gebruiker niet gevonden"));

        if (enabled == null) {
            throw new IllegalArgumentException("Enabled mag niet null zijn");
        }

        user.setEnabled(enabled);

        User updated = userRepository.save(user);
        return toDetailDTO(updated);
    }

    // DTO mapping

    private AdminUserListDTO toListDTO(User user) {
        AdminUserListDTO dto = new AdminUserListDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setEnabled(user.isEnabled());
        dto.setAddress(user.getAddress());
        return dto;
    }

    private AdminUserDetailDTO toDetailDTO(User user) {
        AdminUserDetailDTO dto = new AdminUserDetailDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAddress(user.getAddress());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
        return dto;
    }
}
