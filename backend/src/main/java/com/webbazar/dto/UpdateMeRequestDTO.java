package com.webbazar.dto;

import jakarta.validation.constraints.Size;

public class UpdateMeRequestDTO {

    @Size(max = 100, message = "Naam mag maximaal 100 tekens zijn")
    private String name;

    @Size(max = 255, message = "Adres mag maximaal 255 tekens zijn")
    private String address;

    // als gebruiker een nieuw wachtwoord invult
    @Size(min = 8, message = "Nieuw wachtwoord moet minimaal 8 tekens bevatten")
    private String newPassword;

    public UpdateMeRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
