package com.webbazar.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String type; // "koop" of "huur"
    private List<Long> productIds;
}
