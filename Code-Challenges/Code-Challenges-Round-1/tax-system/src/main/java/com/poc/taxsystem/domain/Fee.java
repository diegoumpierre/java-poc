package com.poc.taxsystem.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Fee {
    private Integer id;
    private StateEnum stateEnum;
    private Integer year;
    private Double value;
}