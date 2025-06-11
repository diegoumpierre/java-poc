package br.dev.guereguere.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int status;
    private long timestamp = new Date().getTime();
    private String path = "/login";
    private String errorMessage;
    private String errorExceptionClass;
    private String token;

    public LoginResponseDTO(int status, String token) {
        this.status = status;
        this.token = token;
    }


    public LoginResponseDTO(int status, String errorExceptionMessage,String errorExceptionClass) {
        this.status = status;
        this.errorMessage = errorExceptionMessage;
        this.errorExceptionClass = errorExceptionClass;
    }



}