package br.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MockUsingAtTest {

    @Mock
    List<String> list;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMockList() {
        Mockito.when(list.get(0)).thenReturn("mocked");
        assertEquals("mocked", list.get(0));
    }
}