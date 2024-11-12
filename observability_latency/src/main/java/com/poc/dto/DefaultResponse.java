package com.poc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultResponse {

    private String errorMessage;
    private Object objectValue;


}
