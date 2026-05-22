package com.webbazar.integration;

import com.webbazar.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

// Integratietest voor JWT validatie

class CheckoutFlowIT {

    @Test
    void token_roundtrip_ok() {
        //  32+ chars secret key voor JWT
        JwtUtil util = new JwtUtil("SuperSecretSuperSecretSuperSecret123456", 3600000);
        UserDetails user = User.withUsername("user@webbazar.test")
                .password("x")
                .roles("USER")
                .build();

        String token = util.generateToken(user);
        assertThat(token).isNotBlank();

        String subject = util.extractUsername(token);
        assertThat(subject).isEqualTo("user@webbazar.test");

        boolean valid = util.validate(token, user);
        assertThat(valid).isTrue();
    }
}
