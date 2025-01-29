package com.poc.taxsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeeDTO {
    private String state;
    private Integer year;
    private Double value;

}
