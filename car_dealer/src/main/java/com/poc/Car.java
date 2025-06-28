package com.poc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Car {
    private String maker;
    private String model;
    private int year;
    private Double price;
    private int miles;
}