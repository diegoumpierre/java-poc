package com.poc.taxsystem.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Product {
    private String name;
    private Double value;
    private List<Fee> feeList;

}

