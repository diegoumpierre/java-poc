package br.dev.guereguere.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MockitoAssertTest {

    //mock
    @Test
    void mockListSize() {
        List<String> mockedList = Mockito.mock(List.class);
        when(mockedList.size()).thenReturn(5);

        assertThat(mockedList.size()).isEqualTo(5);
    }

    //spy
    @Test
    void mockListSpy(){
        List<String> realList = new ArrayList<>();
        List<String> spyList = Mockito.spy(realList);

        spyList.add("one item");
        spyList.add("second item");

        Mockito.verify(spyList).add("one item");

    }




}