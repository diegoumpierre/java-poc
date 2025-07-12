package com.poc.model.enums;

public enum MessagesEnum {
    LOGIN_SUCCESS("LOGIN001", "User or Email invalid"),
    LOGIN_FAIL("LOGIN002", "User or Email invalid"),

    ACCOUNT_EXPIRED("",""),
    ACCOUNT_LOCKED("",""),
    ACCOUNT_CREDENTIALS_EXPIRED("",""),

    ACCOUNT_NOT_ENABLED("",""),

    ;

    private final String code;
    private final String msg;
    MessagesEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}
