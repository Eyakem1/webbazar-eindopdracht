package com.webbazar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @Email(message = "Voer een geldig e-mailadres in")
    @NotBlank(message = "E-mailadres is verplicht")
    private String email;

    @NotBlank(message = "Wachtwoord is verplicht")
    private String password;

    public LoginRequestDTO() {}

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
