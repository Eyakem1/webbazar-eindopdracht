package com.webbazar.controller;

import com.webbazar.dto.LoginRequestDTO;
import com.webbazar.dto.LoginResponseDTO;
import com.webbazar.dto.RegisterRequestDTO;
import com.webbazar.dto.MeResponseDTO;
import com.webbazar.dto.UpdateMeRequestDTO;
import com.webbazar.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    //  gegevens van ingelogde gebruiker ophalen
    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> getMe(Authentication authentication) {
        String email = authentication.getName(); //
        MeResponseDTO me = authService.getMe(email);
        return ResponseEntity.ok(me);
    }

    //  gegevens van ingelogde gebruiker bijwerken
    @PutMapping("/me")
    public ResponseEntity<MeResponseDTO> updateMe(
            Authentication authentication,
            @Valid @RequestBody UpdateMeRequestDTO dto
    ) {
        String email = authentication.getName();
        MeResponseDTO me = authService.updateMe(email, dto);
        return ResponseEntity.ok(me);
    }
}
