package com.webbazar.service;

import com.webbazar.model.Role;
import com.webbazar.model.User;
import com.webbazar.repository.RoleRepository;
import com.webbazar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    public void testFindByEmail_existingUser() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    public void testFindByEmail_userNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("unknown@example.com");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testExistsByEmail_true() {
        when(userRepository.existsByEmail("yes@example.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("yes@example.com"));
    }

    @Test
    public void testExistsByEmail_false() {
        when(userRepository.existsByEmail("no@example.com")).thenReturn(false);
        assertFalse(userService.existsByEmail("no@example.com"));
    }

    @Test
    public void testSaveUser_setsEncodedPasswordAndDefaultRole() {
        // Arrange
        User user = new User();
        user.setPassword("plain123");

        Role role = new Role();
        role.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("plain123")).thenReturn("encoded123");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        User result = userService.saveUser(user);

        // Assert
        assertEquals("encoded123", result.getPassword());
        assertNotNull(result.getRoles());
        assertTrue(result.getRoles().contains(role));
    }
}
