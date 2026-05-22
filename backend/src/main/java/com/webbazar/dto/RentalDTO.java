package com.webbazar.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class RentalDTO {
    private Long id;
    private Instant startDate;
    private Instant endDate;
}
