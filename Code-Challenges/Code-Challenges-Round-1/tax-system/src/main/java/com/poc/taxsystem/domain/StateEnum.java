package com.poc.taxsystem.domain;


import java.util.Arrays;

public enum StateEnum {
    RS("RS"),
    SC("SC"),
    RJ("RJ"),
    SP("SP");

    private final String stringCode;
    public String getStringCode() {
        return this.stringCode;
    }

    private StateEnum(String stringCode) {
        this.stringCode = stringCode;
    }

    public static StateEnum toEnum(String stringCode) {
        if (stringCode == null) {
            return null;
        } else {
            for(StateEnum x : values()) {
                if (stringCode.equals(x.getStringCode())) {
                    return x;
                }
            }

            throw new IllegalArgumentException("Invalid ID '" + stringCode + "' to state, please use {" + Arrays.toString(values()) + "}");
        }
    }
}
