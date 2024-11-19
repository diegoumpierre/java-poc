package com.poc.domain;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Product {
    private String name;
    private Double value;
    private List<Fee> feeList;
}