package com.webbazar.dto.admin;

import lombok.Data;

@Data
public class AdminUserListDTO {
    private Long id;
    private String email;
    private String name;
    private boolean enabled;


    private String address;
}
