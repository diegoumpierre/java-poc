package br.com.poc.atomic;

public class Counter {

    private Integer counter=0;
    public void increment(){
        counter++;
    }
    public Integer getValue(){
        return counter;
    }


}
