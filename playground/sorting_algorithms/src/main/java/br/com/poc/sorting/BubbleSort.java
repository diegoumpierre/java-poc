package br.com.poc.sorting;

import java.text.MessageFormat;

public class BubbleSort extends PrintForSorting{

    int changes = 0;
    int comparison = 0;
    public int[] sort(int[] arrayToSort){

        this.startTime();

        int aux;
        for(int i=0; i < arrayToSort.length; i++){

            for(int j=0; j < arrayToSort.length -1; j++){
                comparison++;
                if (arrayToSort[j] > arrayToSort[j+1]){
                    aux = arrayToSort[j];
                    arrayToSort[j] = arrayToSort[j+1];
                    arrayToSort[j+1] = aux;
                    this.changes ++;
                }
            }
        }

        this.printTime("BubbleSort",changes,comparison,arrayToSort.length);

        return arrayToSort;
    }
}
