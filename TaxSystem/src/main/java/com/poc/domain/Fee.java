package com.poc.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Fee {

    private StateEnum stateEnum;
    private Integer year;
    private Double value;

}
