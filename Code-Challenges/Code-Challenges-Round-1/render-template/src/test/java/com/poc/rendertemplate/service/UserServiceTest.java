package com.poc.rendertemplate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        this.userService = new UserService();
    }

    @Test
    void printUsersPDFShouldSuccess() {
        userService.allUsers(PrintFormatEnum.PDF);
    }

    @Test
    void printUsersCSVShouldSuccess() {
        userService.allUsers(PrintFormatEnum.CSV);
    }

    @Test
    void printUsersHTMLShouldSuccess() {
        userService.allUsers(PrintFormatEnum.HTML);
    }

}