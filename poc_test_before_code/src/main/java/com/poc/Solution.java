package com.poc;

import com.poc.model.LoginModel;
import com.poc.model.enums.MessagesEnum;
import com.poc.service.AuthorizathionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Solution {

    AuthorizathionService authorizathionService;


    /*
    Send messages to the screen user
     */
    public List<MessagesEnum> logon(LoginModel loginModel){

        List<MessagesEnum> messagesEnums = new ArrayList<MessagesEnum>();
        SecurityUserAuthorization securityUserAuthorization = authorizathionService.logon(loginModel.getEmail(), loginModel.getPassword());

        if (securityUserAuthorization != null){
            if (securityUserAuthorization.isEnabled()) {
                //success
                messagesEnums.add(MessagesEnum.LOGIN_SUCCESS);
            }else{
                messagesEnums.add(MessagesEnum.ACCOUNT_EXPIRED);
            }
        }else{
            messagesEnums.add(MessagesEnum.LOGIN_FAIL);
        }

        return messagesEnums;
    }


}
