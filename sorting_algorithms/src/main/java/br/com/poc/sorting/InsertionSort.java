package br.com.poc.sorting;

public class InsertionSort extends PrintForSorting{

    int changes = 0;
    int comparison = 0;
    public int[] sort(int[] arrayToSort){
        this.startTime();

        int key;
        int j;

        for (int i = 1; i < arrayToSort.length; i++ ){
            key = arrayToSort[i];
            comparison++;
            for(j = i - 1; (j >= 0) && (arrayToSort[j] > key); j--){
                comparison++;
                arrayToSort[j+1] = arrayToSort[j];
                changes++;
            }
            arrayToSort[j+1] = key;
        }

        this.printTime("InsertionSort",changes,comparison,arrayToSort.length);

        return arrayToSort;
    }

}