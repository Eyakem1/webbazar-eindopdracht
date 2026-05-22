package com.webbazar.service;

import com.webbazar.dto.LoginRequestDTO;
import com.webbazar.dto.LoginResponseDTO;
import com.webbazar.dto.RegisterRequestDTO;
import com.webbazar.dto.MeResponseDTO;
import com.webbazar.dto.UpdateMeRequestDTO;
import com.webbazar.entity.Role;
import com.webbazar.entity.User;
import com.webbazar.repo.RoleRepository;
import com.webbazar.repo.UserRepository;
import com.webbazar.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public LoginResponseDTO login(LoginRequestDTO req) {
        User u = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ongeldige login"));

        if (!encoder.matches(req.getPassword(), u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ongeldige login");
        }
        if (!u.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account gedeactiveerd");
        }

        // Build UserDetails
        var authorities = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(authorities)
                .disabled(!u.isEnabled())
                .build();

        String token = jwt.generateToken(userDetails);

        LoginResponseDTO res = new LoginResponseDTO();
        res.setToken(token);
        res.setRoles(u.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        res.setName(u.getName());
        return res;
    }

    public void register(RegisterRequestDTO req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email bestaat al");
        }

        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setEnabled(true);
        u.setName(req.getName());
        u.setAddress(req.getAddress());

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepo.findByName("ROLE_USER").orElseThrow(
                () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_USER ontbreekt")
        ));
        u.setRoles(roles);

        userRepo.save(u);
    }

    // Profiel ophalen
    public MeResponseDTO getMe(String email) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));

        return new MeResponseDTO(
                u.getEmail(),
                u.getName(),
                u.getAddress()
        );
    }

    // Profiel bijwerken
    public MeResponseDTO updateMe(String email, UpdateMeRequestDTO dto) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            u.setName(dto.getName());
        }

        if (dto.getAddress() != null) {
            u.setAddress(dto.getAddress());
        }

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            u.setPassword(encoder.encode(dto.getNewPassword()));
        }

        userRepo.save(u);

        return new MeResponseDTO(
                u.getEmail(),
                u.getName(),
                u.getAddress()
        );
    }
}
