package com.poc.domain;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Product {
    private String name;
    private Double value;
    private List<Fee> feeList = new ArrayList<>();
}