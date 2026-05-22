package com.webbazar.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails user;

    @BeforeEach
    void setUp() {
        // Gebruik een test-secret (moet minimaal 32 tekens zijn voor HS256)
        jwtUtil = new JwtUtil("test-secret-key-test-secret-key-1234", 3600000);
        user = User.withUsername("testuser@example.com")
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    void generate_and_extract_username_should_match() {
        String token = jwtUtil.generateToken(user);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser@example.com", username);
    }

    @Test
    void validate_token_should_return_true_for_correct_user() {
        String token = jwtUtil.generateToken(user);
        assertTrue(jwtUtil.validate(token, user));
    }

    @Test
    void token_should_expire_after_expiration_time() throws InterruptedException {
        JwtUtil shortLived = new JwtUtil("test-secret-key-test-secret-key-1234", 1); // 1ms
        String token = shortLived.generateToken(user);
        Thread.sleep(5); // token laten verlopen
        assertFalse(shortLived.validate(token, user));
    }

    @Test
    void extractUsernameFromHeader_should_strip_bearer_prefix() {
        String token = jwtUtil.generateToken(user);
        String header = "Bearer " + token;
        String username = jwtUtil.extractUsernameFromHeader(header);
        assertEquals("testuser@example.com", username);
    }

    @Test
    void extractUsernameFromHeader_should_throw_for_invalid_header() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.extractUsernameFromHeader("InvalidHeader"));
        assertTrue(ex.getMessage().toLowerCase().contains("invalid"));
    }
}
