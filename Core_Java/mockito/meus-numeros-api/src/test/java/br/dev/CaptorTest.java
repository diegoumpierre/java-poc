package br.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CaptorTest {

    @Mock
    private List<String> list;

    @Captor
    ArgumentCaptor<String> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCaptor() {
        list.add("Mockito");
        Mockito.verify(list).add(captor.capture());
        assertEquals("Mockito", captor.getValue());
    }

}
