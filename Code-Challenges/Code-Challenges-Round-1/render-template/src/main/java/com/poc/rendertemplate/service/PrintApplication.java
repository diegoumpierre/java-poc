package com.poc.rendertemplate.service;

import com.poc.rendertemplate.dto.User;

import java.util.List;

public interface PrintApplication {
    void printFile(List<User> userList);
}
