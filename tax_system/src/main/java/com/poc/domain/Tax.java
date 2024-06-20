package com.poc.domain;

public class Tax {

    private StateEnum state;
    private Double value;
    private Integer year;

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
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


    public enum StateEnum {
        RS,
        SC,
        RJ,
        SP
    }


}
