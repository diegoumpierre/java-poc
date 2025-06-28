package com.poc.library.service.messages;

public enum LibraryMessages {

    RESERVED_ANOTHER_PERSON("001", "The book is reserved to another person."),
    BOOK_NOT_AVALIABLE_BORROW("002", "The book is not available!"),
    BOOK_ALREADY_RESERVED_THESE_PERSON("003", "The book is reserved for this person.");



    private final String code;
    private final String msg;

    LibraryMessages(String code, String msg) {
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