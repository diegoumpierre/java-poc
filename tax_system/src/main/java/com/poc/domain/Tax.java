package com.poc.domain;

public class Tax {

    private String state;
    private Double value;
    private Integer year;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
