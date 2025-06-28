package br.com.poc.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterAtomic {

    private AtomicInteger counter2 = new AtomicInteger(0);
    public void increment2(){
        counter2.incrementAndGet();
    }
    public Integer getValue2(){
        return counter2.get();
    }

}
