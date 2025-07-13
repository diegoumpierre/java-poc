package br.com.poc.sorting;

public class SelectionSort extends PrintForSorting{

    int changes = 0;
    int comparison = 0;

    public int[] sort(int[] arrayToSort){
        this.startTime();
        int aux;

        for (int fixed=0; fixed < arrayToSort.length -1; fixed++){
            comparison++;
            int less = fixed;

            for(int i = less +1; i < arrayToSort.length; i++){
                comparison++;
                if (arrayToSort[i] < arrayToSort[less]){
                    less = i;

                }
            }
            if (less != fixed){
                comparison++;
                aux = arrayToSort[fixed];
                arrayToSort[fixed] = arrayToSort[less];
                arrayToSort[less] = aux;
                changes++;
            }

        }
        this.printTime("SelectionSort",changes,comparison, arrayToSort.length);
        return arrayToSort;
    }
}
