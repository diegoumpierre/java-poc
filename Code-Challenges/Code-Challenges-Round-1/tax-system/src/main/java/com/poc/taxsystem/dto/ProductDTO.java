package com.poc.taxsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private Double value;
    private String state;
    private Integer year;
}
