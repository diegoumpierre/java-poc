package br.dev.guereguere.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MockitoAssertTest {

    //@Mock
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
    //@Mock end


    //mock
    @Test
    void mockListSize() {
        List<String> mockedList = Mockito.mock(List.class);
        when(mockedList.size()).thenReturn(5);

        assertThat(mockedList.size()).isEqualTo(5);
    }
    //mock end

    //spy
    @Test
    void mockListSpy(){
        List<String> realList = new ArrayList<>();
        List<String> spyList = Mockito.spy(realList);

        spyList.add("one item");
        spyList.add("second item");

        Mockito.verify(spyList).add("one item");
    }
    //spy end

    //@Spy
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
    //@Spy end




}