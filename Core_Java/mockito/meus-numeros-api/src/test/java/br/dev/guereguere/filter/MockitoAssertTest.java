package br.dev.guereguere.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MockitoAssertTest {

    @Test
    void mockListSize() {
        List<String> mockedList = Mockito.mock(List.class);
        when(mockedList.size()).thenReturn(5);

        assertThat(mockedList.size()).isEqualTo(5);
    }
}