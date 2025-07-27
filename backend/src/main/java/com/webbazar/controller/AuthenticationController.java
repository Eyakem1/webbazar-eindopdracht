package com.webbazar.controller;

import com.webbazar.dto.UserRegistrationDTO;
import com.webbazar.model.Role;
import com.webbazar.model.User;
import com.webbazar.repository.RoleRepository;
import com.webbazar.repository.UserRepository;
import com.webbazar.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenProvider jwtTokenProvider,
                                    UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            if (!user.isEnabled()) {
                return ResponseEntity.status(403).body("Account is gedeactiveerd");
            }

            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            String token = jwtTokenProvider.createToken(user.getEmail(), roleNames);

            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "roles", roleNames,
                    "token", token
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Ongeldige inloggegevens");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Interne serverfout: " + ex.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO dto) {
        try {
            if (userRepository.existsByEmail(dto.getEmail())) {
                return ResponseEntity.badRequest().body("Gebruiker met dit e-mailadres bestaat al");
            }

            User user = new User();
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setName(dto.getName());
            user.setAddress(dto.getAddress());
            user.setEnabled(true);

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER niet gevonden"));

            user.setRoles(Set.of(userRole));
            userRepository.save(user);

            return ResponseEntity.ok("Gebruiker succesvol geregistreerd");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registratiefout: " + e.getMessage());
        }
    }
}
