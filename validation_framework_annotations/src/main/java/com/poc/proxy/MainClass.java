package com.poc.proxy;

public class MainClass {


    public static void main(String[] args) {
        ExpensiveObject object = new ExpensiveObjectProxy();
        object.process();
//        object.process();
    }

}
