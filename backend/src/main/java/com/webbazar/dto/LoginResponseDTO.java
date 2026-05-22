package com.webbazar.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponseDTO {
    private String token;
    private String email;
    private String name;
    private boolean enabled;
    private List<String> roles;
}
