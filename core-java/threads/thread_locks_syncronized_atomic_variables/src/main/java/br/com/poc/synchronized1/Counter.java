package br.com.poc.synchronized1;

public class Counter {

    private Integer counter=0;
    public synchronized void increment(){
        counter++;
    }
    public synchronized Integer getValue(){
        return counter;
    }


}
