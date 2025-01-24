package com.poc.observability.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricDto {

    private boolean success;
    private long responseTime;
    private long responseTimeStart;
    private long responseTimeEnd;
    private String method;
    private String path;




}
