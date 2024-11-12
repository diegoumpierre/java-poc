package com.poc.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Fee implements Comparable<Fee> {

    private Integer id;
    private StateEnum stateEnum;
    private Integer year;
    private Double value;

    @Override
    public int compareTo(Fee other) {
        if (this.getId() > other.getId()) return 1;
        if (this.getId().equals(other.getId())) return 0;
        return -1;
    }
}
