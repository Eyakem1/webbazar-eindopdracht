package com.webbazar.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateUserRequestDTO {

    @NotBlank
    private String name;

    private String address;

    private Boolean enabled;


    @Size(min = 6, max = 100)
    private String password;
}
