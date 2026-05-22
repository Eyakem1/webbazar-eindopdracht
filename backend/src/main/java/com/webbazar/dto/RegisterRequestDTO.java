package com.webbazar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @Email(message = "Voer een geldig e-mailadres in")
    @NotBlank(message = "E-mailadres is verplicht")
    private String email;

    @NotBlank(message = "Wachtwoord is verplicht")
    @Size(min = 8, message = "Wachtwoord moet minimaal 8 tekens bevatten")
    private String password;

    @NotBlank(message = "Naam is verplicht")
    @Size(max = 100, message = "Naam mag maximaal 100 tekens zijn")
    private String name;

    @Size(max = 255, message = "Adres mag maximaal 255 tekens zijn")
    private String address;

    public RegisterRequestDTO() {}

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
