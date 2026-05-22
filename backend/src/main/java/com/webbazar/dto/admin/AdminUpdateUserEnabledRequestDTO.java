package com.webbazar.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateUserEnabledRequestDTO {

    @NotNull
    private Boolean enabled;
}
