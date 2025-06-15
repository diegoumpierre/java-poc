package br.dev;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class SpyTest {

    @Test
    void mockListSpy(){
        List<String> realList = new ArrayList<>();
        List<String> spyList = Mockito.spy(realList);

        spyList.add("one item");
        spyList.add("second item");

        Mockito.verify(spyList).add("one item");
    }

}
