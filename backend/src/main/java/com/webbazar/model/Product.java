package com.webbazar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel is verplicht")
    private String title;

    @NotBlank(message = "Auteur is verplicht")
    private String author;

    @Size(max = 1000, message = "Beschrijving mag maximaal 1000 tekens bevatten")
    private String description;

    @PositiveOrZero(message = "Prijs mag niet negatief zijn")
    private double price;

    @PositiveOrZero(message = "Huurprijs mag niet negatief zijn")
    private double rentalPrice;

    // Niet verplicht bij aanmaken — alleen als er een bestand wordt meegegeven
    private String fileType;

    private String filePath;
}
