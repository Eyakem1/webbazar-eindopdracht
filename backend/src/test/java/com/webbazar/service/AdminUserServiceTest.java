package com.webbazar.service;

import com.webbazar.dto.admin.AdminCreateUserRequestDTO;
import com.webbazar.dto.admin.AdminUpdateUserRequestDTO;
import com.webbazar.entity.Role;
import com.webbazar.entity.User;
import com.webbazar.repo.RoleRepository;
import com.webbazar.repo.UserRepository;
import com.webbazar.service.impl.AdminUserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AdminUserServiceImpl adminUserService;

    @Test
    void getAllUsers_mapsUsersToListDtos() {
        when(userRepository.findAll()).thenReturn(List.of(
                user(1L, "a@example.com", true),
                user(2L, "b@example.com", false)
        ));

        var users = adminUserService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getEmail()).isEqualTo("a@example.com");
        assertThat(users.get(1).isEnabled()).isFalse();
    }

    @Test
    void getUserById_existingUser_mapsDetailDtoWithRoles() {
        User user = user(1L, "admin@example.com", true);
        user.setRoles(Set.of(
                Role.builder().name("ROLE_ADMIN").build(),
                Role.builder().name("ROLE_USER").build()
        ));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var dto = adminUserService.getUserById(1L);

        assertThat(dto.getEmail()).isEqualTo("admin@example.com");
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void getUserById_missingUser_throwsEntityNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.getUserById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createUser_normalizesEmailAndSavesRoleUser() {
        AdminCreateUserRequestDTO request = createRequest(" NEW@EXAMPLE.COM ", true);
        Role role = Role.builder().name("ROLE_USER").build();

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret123")).thenReturn("hash");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        var dto = adminUserService.createUser(request);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getEmail()).isEqualTo("new@example.com");
        assertThat(dto.isEnabled()).isTrue();
        assertThat(dto.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void createUser_withNullEnabled_defaultsToEnabled() {
        AdminCreateUserRequestDTO request = createRequest("new@example.com", null);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(Role.builder().name("ROLE_USER").build()));
        when(passwordEncoder.encode("secret123")).thenReturn("hash");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = adminUserService.createUser(request);

        assertThat(dto.isEnabled()).isTrue();
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsIllegalArgument() {
        AdminCreateUserRequestDTO request = createRequest("taken@example.com", true);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> adminUserService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bestaat al");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_withoutRoleUser_throwsIllegalState() {
        AdminCreateUserRequestDTO request = createRequest("new@example.com", true);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.createUser(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ROLE_USER");
    }

    @Test
    void updateUser_updatesBasicFieldsEnabledAndPassword() {
        User user = user(1L, "user@example.com", true);

        AdminUpdateUserRequestDTO request = new AdminUpdateUserRequestDTO();
        request.setName("Updated");
        request.setAddress("Updated address");
        request.setEnabled(false);
        request.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("newHash");
        when(userRepository.save(user)).thenReturn(user);

        var dto = adminUserService.updateUser(1L, request);

        assertThat(dto.getName()).isEqualTo("Updated");
        assertThat(dto.getAddress()).isEqualTo("Updated address");
        assertThat(dto.isEnabled()).isFalse();
        assertThat(user.getPassword()).isEqualTo("newHash");
    }

    @Test
    void updateUser_withNullEnabledAndBlankPassword_keepsThoseFields() {
        User user = user(1L, "user@example.com", true);
        user.setPassword("oldHash");

        AdminUpdateUserRequestDTO request = new AdminUpdateUserRequestDTO();
        request.setName("Updated");
        request.setAddress("Address");
        request.setEnabled(null);
        request.setPassword("   ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        var dto = adminUserService.updateUser(1L, request);

        assertThat(dto.isEnabled()).isTrue();
        assertThat(user.getPassword()).isEqualTo("oldHash");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUser_missingUser_throwsEntityNotFound() {
        AdminUpdateUserRequestDTO request = new AdminUpdateUserRequestDTO();
        request.setName("Updated");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.updateUser(99L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteUser_existingUser_deletesById() {
        when(userRepository.existsById(1L)).thenReturn(true);

        adminUserService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_missingUser_throwsEntityNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> adminUserService.deleteUser(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteUser_withRelatedData_throwsIllegalState() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("FK"))
                .when(userRepository).deleteById(1L);

        assertThatThrownBy(() -> adminUserService.deleteUser(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("gerelateerde data");
    }

    @Test
    void updateUserEnabled_updatesFlag() {
        User user = user(1L, "user@example.com", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        var dto = adminUserService.updateUserEnabled(1L, false);

        assertThat(dto.isEnabled()).isFalse();
    }

    @Test
    void updateUserEnabled_withNullValue_throwsIllegalArgument() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user(1L, "user@example.com", true)));

        assertThatThrownBy(() -> adminUserService.updateUserEnabled(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    void updateUserEnabled_missingUser_throwsEntityNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.updateUserEnabled(99L, true))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private AdminCreateUserRequestDTO createRequest(String email, Boolean enabled) {
        AdminCreateUserRequestDTO request = new AdminCreateUserRequestDTO();
        request.setEmail(email);
        request.setPassword("secret123");
        request.setName("User");
        request.setAddress("Address");
        request.setEnabled(enabled);
        return request;
    }

    private User user(Long id, String email, boolean enabled) {
        return User.builder()
                .id(id)
                .email(email)
                .password("hash")
                .name("Name")
                .address("Address")
                .enabled(enabled)
                .roles(Set.of(Role.builder().name("ROLE_USER").build()))
                .build();
    }
}