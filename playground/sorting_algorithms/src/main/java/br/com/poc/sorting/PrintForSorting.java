package br.com.poc.sorting;

import java.text.MessageFormat;

public class PrintForSorting {

    //messages for the time comparison
    private final String strTime = "{2} | Time = {0} ms | Average = {1} ms";
    private final String strQuantity = " | Quantity = {0} | Changes = {1} | Comparison = {2}";

    protected int arraySize = 0;

    //log the times
    long startTime;


    protected void startTime(){
        startTime = System.currentTimeMillis();
    }

    protected void printTime(String sortAlgorithmName, int changes, int comparison, int arraySize){
        //Print the times
        long endTime = System.currentTimeMillis();
        long time = (endTime - startTime);
        if (arraySize == 0) arraySize = 1;
        System.out.print(MessageFormat.format(strTime  ,time,time/arraySize,sortAlgorithmName));
        System.out.println(MessageFormat.format(strQuantity,arraySize,changes,comparison));
    }

}
