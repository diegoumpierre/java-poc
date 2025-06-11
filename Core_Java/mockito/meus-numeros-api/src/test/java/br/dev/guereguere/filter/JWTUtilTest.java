package br.dev.guereguere.filter;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {

    JWTUtil jwtUtil = new JWTUtil();

    @Test
    void generateToken() {
        String tokenGerado = jwtUtil.generateToken("diego@umpierre.com.br");
        System.out.println(tokenGerado);
        jwtUtil.validToken(tokenGerado);
    }

    @Test
    void validToken() {
    }

    @Test
    void getUsername() {
    }
}