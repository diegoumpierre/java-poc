package br.dev.guereguere.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.dev.guereguere.dto.LoginRequestDTO;
import br.dev.guereguere.dto.LoginResponseDTO;
import br.dev.guereguere.entity.SecurityUserDetails;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private JWTUtil jwtUtil;

    private ObjectMapper mapper = new ObjectMapper();


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {

        try {
            LoginRequestDTO loginRequestDTO = new ObjectMapper()
                    .readValue(req.getInputStream(), LoginRequestDTO.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                                                        loginRequestDTO.getEmail(),
                                                                        loginRequestDTO.getSenha(),
                                                                        new ArrayList<>());


            Authentication auth = authenticationManager.authenticate(authToken);
            return auth;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String username = ((SecurityUserDetails) auth.getPrincipal()).getUsername();
        String token = JWTUtil.TOKEN_START + jwtUtil.generateToken(username);
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(200, token);
        response.addHeader("Authorization", loginResponseDTO.getToken());
        response.addHeader("access-control-expose-headers", "Authorization");
        response.setContentType("application/json");
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        response.getWriter().append(mapper.writeValueAsString(loginResponseDTO));

    }

    private class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(401,exception.getMessage(),exception.getClass().getSimpleName());
            response.setStatus(401);
            response.setContentType("application/json");
            //response.getWriter().append(json(exception.getMessage()));
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            response.getWriter().append(mapper.writeValueAsString(loginResponseDTO));
        }

        private String json(String messageDetails) {

            /*
            User account is locked - LockedException
            User is disabled
            User account has expired
            User credentials have expired - CredentialsExpiredException
             */


            long date = new Date().getTime();
            return "{\"timestamp\": " + date + ", "
                    + "\"status\": 401, "
                    + "\"error\": \"Não autorizado - \", "+ messageDetails +" - "
                    + "\"message\": \"Email ou senha inválidos\", "
                    + "\"path\": \"/login\"}";
        }
    }
}