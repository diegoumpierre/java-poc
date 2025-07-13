package br.dev.InjectMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    @Mock
    UserRepository repository;

    @InjectMocks
    UserService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInjection() {
        Mockito.when(repository.findUsernameById(1)).thenReturn("Diego");
        assertEquals("Diego", service.getUsernameById(1));
    }

}