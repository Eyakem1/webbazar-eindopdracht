package com.webbazar.dto.admin;

import lombok.Data;

import java.util.Set;

@Data
public class AdminUserDetailDTO {
    private Long id;
    private String email;
    private String name;
    private String address;
    private boolean enabled;
    private Set<String> roles;
}
