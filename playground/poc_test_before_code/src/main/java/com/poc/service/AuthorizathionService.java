package com.poc.service;

import com.poc.SecurityUserAuthorization;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthorizathionService {


    public SecurityUserAuthorization logon(String email, String  password){
        //call the rest api to external service
        return null;

    }



}
