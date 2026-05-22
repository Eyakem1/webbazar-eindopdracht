package com.webbazar.service;

import com.webbazar.dto.LoginRequestDTO;
import com.webbazar.dto.RegisterRequestDTO;
import com.webbazar.dto.UpdateMeRequestDTO;
import com.webbazar.entity.Role;
import com.webbazar.entity.User;
import com.webbazar.repo.RoleRepository;
import com.webbazar.repo.UserRepository;
import com.webbazar.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepo;
    @Mock RoleRepository roleRepo;
    @Mock PasswordEncoder encoder;
    @Mock JwtUtil jwt;

    @InjectMocks AuthService authService;

    @Test
    void login_withValidCredentials_returnsTokenNameAndRoles() {
        Role role = Role.builder().name("ROLE_USER").build();

        User user = User.builder()
                .email("test@example.com")
                .password("hash")
                .name("Test User")
                .enabled(true)
                .roles(Set.of(role))
                .build();

        LoginRequestDTO req = loginRequest("test@example.com", "secret");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("secret", "hash")).thenReturn(true);
        when(jwt.generateToken(any())).thenReturn("jwt-token");

        var response = authService.login(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getName()).isEqualTo("Test User");
        assertThat(response.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void login_withUnknownEmail_throwsUnauthorized() {
        when(userRepo.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest("missing@example.com", "secret")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401 UNAUTHORIZED");
    }

    @Test
    void login_withWrongPassword_throwsUnauthorized() {
        User user = User.builder()
                .email("test@example.com")
                .password("hash")
                .enabled(true)
                .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest("test@example.com", "wrong")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401 UNAUTHORIZED");

        verify(jwt, never()).generateToken(any());
    }

    @Test
    void login_withDisabledUser_throwsForbidden() {
        User user = User.builder()
                .email("test@example.com")
                .password("hash")
                .enabled(false)
                .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("secret", "hash")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(loginRequest("test@example.com", "secret")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    @Test
    void register_withNewEmail_savesUserWithRoleUser() {
        RegisterRequestDTO req = registerRequest("new@example.com", "plain", "New User", "Street 1");
        Role role = Role.builder().name("ROLE_USER").build();

        when(userRepo.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(encoder.encode("plain")).thenReturn("encoded");

        authService.register(req);

        verify(userRepo).save(argThat(user ->
                user.getEmail().equals("new@example.com")
                        && user.getPassword().equals("encoded")
                        && user.isEnabled()
                        && user.getName().equals("New User")
                        && user.getAddress().equals("Street 1")
                        && user.getRoles().contains(role)
        ));
    }

    @Test
    void register_withExistingEmail_throwsBadRequest() {
        RegisterRequestDTO req = registerRequest("used@example.com", "plain", "Used", null);

        when(userRepo.existsByEmail("used@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");

        verify(userRepo, never()).save(any());
    }

    @Test
    void register_withoutRoleUser_throwsInternalServerError() {
        RegisterRequestDTO req = registerRequest("new@example.com", "plain", "New", null);

        when(userRepo.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.empty());
        when(encoder.encode("plain")).thenReturn("encoded");

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("500 INTERNAL_SERVER_ERROR");
    }

    @Test
    void getMe_returnsCurrentUserData() {
        User user = User.builder()
                .email("me@example.com")
                .name("Me")
                .address("Home")
                .build();

        when(userRepo.findByEmail("me@example.com")).thenReturn(Optional.of(user));

        var me = authService.getMe("me@example.com");

        assertThat(me.getEmail()).isEqualTo("me@example.com");
        assertThat(me.getName()).isEqualTo("Me");
        assertThat(me.getAddress()).isEqualTo("Home");
    }

    @Test
    void getMe_unknownUser_throwsNotFound() {
        when(userRepo.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getMe("missing@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND");
    }

    @Test
    void updateMe_updatesOnlyProvidedFieldsAndPassword() {
        User user = User.builder()
                .email("me@example.com")
                .name("Old")
                .address("Old Address")
                .password("oldHash")
                .build();

        UpdateMeRequestDTO dto = new UpdateMeRequestDTO();
        dto.setName("New");
        dto.setAddress("New Address");
        dto.setNewPassword("newPassword");

        when(userRepo.findByEmail("me@example.com")).thenReturn(Optional.of(user));
        when(encoder.encode("newPassword")).thenReturn("newHash");

        var result = authService.updateMe("me@example.com", dto);

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getAddress()).isEqualTo("New Address");
        assertThat(user.getPassword()).isEqualTo("newHash");

        verify(userRepo).save(user);
    }

    @Test
    void updateMe_ignoresBlankNameAndBlankPasswordButAllowsNullAddress() {
        User user = User.builder()
                .email("me@example.com")
                .name("Old")
                .address("Old Address")
                .password("oldHash")
                .build();

        UpdateMeRequestDTO dto = new UpdateMeRequestDTO();
        dto.setName("   ");
        dto.setAddress(null);
        dto.setNewPassword("   ");

        when(userRepo.findByEmail("me@example.com")).thenReturn(Optional.of(user));

        var result = authService.updateMe("me@example.com", dto);

        assertThat(result.getName()).isEqualTo("Old");
        assertThat(result.getAddress()).isEqualTo("Old Address");
        assertThat(user.getPassword()).isEqualTo("oldHash");

        verify(encoder, never()).encode(anyString());
    }

    @Test
    void updateMe_unknownUser_throwsNotFound() {
        UpdateMeRequestDTO dto = new UpdateMeRequestDTO();

        when(userRepo.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateMe("missing@example.com", dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND");
    }

    private LoginRequestDTO loginRequest(String email, String password) {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    private RegisterRequestDTO registerRequest(String email, String password, String name, String address) {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail(email);
        req.setPassword(password);
        req.setName(name);
        req.setAddress(address);
        return req;
    }
}