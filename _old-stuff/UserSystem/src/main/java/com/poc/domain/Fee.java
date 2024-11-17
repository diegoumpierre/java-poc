package com.poc.domain;

import lombok.Data;

@Data
public class Fee {

    private StateEnum stateEnum;
    private Integer year;
    private Double value;

}
