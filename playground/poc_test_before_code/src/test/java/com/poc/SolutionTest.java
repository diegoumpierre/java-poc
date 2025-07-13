package com.poc;

import com.poc.model.LoginModel;
import com.poc.model.enums.MessagesEnum;
import com.poc.model.enums.SecurityUserCredentialEnum;
import com.poc.service.AuthorizathionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Jan 12 2024
 */
class SolutionTest {
    private Solution solution;



    @Mock
    AuthorizathionService authorizathionService;


    @BeforeEach
    void init(){
        MockitoAnnotations.initMocks(this);
        solution = new Solution();
    }

    @Test
    void sendValidLoginAndValidPasswordShouldSuccess() {
        LoginModel loginModel = new LoginModel();
        loginModel.setPassword("9090");
        loginModel.setEmail("1010@something.com");

        Whitebox.setInternalState(solution,authorizathionService);
        SecurityUserAuthorization securityUserAuthorization = mock(SecurityUserAuthorization.class);
        when(securityUserAuthorization.isEnabled()).thenReturn(true);
        when(authorizathionService.logon(loginModel.getEmail(), loginModel.getPassword())).thenReturn(securityUserAuthorization);
        assertEquals(MessagesEnum.LOGIN_SUCCESS, solution.logon(loginModel).get(0) );
        verify(authorizathionService,times(1)).logon(loginModel.getEmail(), loginModel.getPassword());

    }

    @Test
    void sendValidLoginAndBadPasswordShouldFail() {
        //    LOGIN_FAIL("LOGIN002", "User or Email invalid"),
        LoginModel loginModel = new LoginModel();
        loginModel.setPassword("9091");
        loginModel.setEmail("1010@something.com");

        Whitebox.setInternalState(solution,authorizathionService);
        when(authorizathionService.logon(loginModel.getEmail(), loginModel.getPassword())).thenReturn(null);
        assertEquals(MessagesEnum.LOGIN_FAIL, solution.logon(loginModel).get(0));
        verify(authorizathionService,times(1)).logon(loginModel.getEmail(), loginModel.getPassword());
    }

    @Test
    void sendValidLoginAndValidPasswordAccountExpiredShouldFail() {
        //    ACCOUNT_EXPIRED("",""),
        LoginModel loginModel = new LoginModel();
        loginModel.setPassword("9090");
        loginModel.setEmail("1010@something.com");

        Whitebox.setInternalState(solution,authorizathionService);
        SecurityUserAuthorization securityUserAuthorization = mock(SecurityUserAuthorization.class);
        securityUserAuthorization.setAccountNonExpired(false);
        when(authorizathionService.logon(loginModel.getEmail(), loginModel.getPassword())).thenReturn(securityUserAuthorization);
        assertEquals(MessagesEnum.ACCOUNT_EXPIRED, solution.logon(loginModel).get(0) );
    }

    @Test
    void sendValidLoginAndValidPasswordAccountLockedShouldFail() {
        //    ACCOUNT_LOCKED("",""),
    }

    @Test
    void sendValidLoginAndValidPasswordAccountCredentialsExpiredShouldFail() {
        //    ACCOUNT_CREDENTIALS_EXPIRED("",""),
    }

    @Test
    void sendValidLoginAndValidPasswordAccountDiabledShouldFail() {
        //    ACCOUNT_NOT_ENABLED("","")
    }

    //I can run huddle with this stuff, I'm not a baby
}