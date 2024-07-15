package com.poc.domain;

public class Tax {

    public enum StateEnum {
        RS,
        SC,
        RJ,
        SP
    }

    public enum MonthEnum {
        JAN,
        FEV,
        MAR,
        APR,
        MAY,
        JUN,
        JUL,
        AUG,
        SET,
        OUT,
        NOV,
        DEC;
    }

    private StateEnum state;
    private Integer year;
    private MonthEnum monthEnum;

    public Tax() {}

}
