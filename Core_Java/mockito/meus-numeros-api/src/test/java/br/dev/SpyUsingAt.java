package br.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpyUsingAt {

    @Spy
    List<String> list2 = new ArrayList<>();

    @BeforeEach
    void setUp2() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSpyList() {
        list2.add("spy");
        Mockito.verify(list2).add("spy");
        assertEquals(1, list2.size());
    }



}
