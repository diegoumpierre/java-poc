package com.umpierre.functionalinterfaces;

import com.umpierre.functionalinterfaces.dataTest.DataForTest;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.function.Consumer;


public class ConsumerTest {

    @Test
    public void printNames(){
        Consumer<String> consumerSimplePrint = t -> System.out.println(t);
        DataForTest.gimmeListNames().stream().forEach(consumerSimplePrint);

    }

    @Test
    public void consumerChain(){
        Consumer<String> consumerSimplePrint = t -> System.out.println(t);

        Consumer<List<String>> consumerUpperCase = list -> {
            for(int i=0; i< list.size(); i++){
                list.set(i, list.get(i).toUpperCase());
            }
        };
        Consumer<List<String>> consumerPrintEachValueUseAnotherConsumer =
                list -> list.stream().forEach(consumerSimplePrint);

        consumerUpperCase.andThen(consumerPrintEachValueUseAnotherConsumer).accept(DataForTest.gimmeListNames());
    }

}
