package br.com.poc.sorting;

public class QuickSort extends PrintForSorting {

    int changes = 0;
    int comparison = 0;

    private int[] quickSort(int[] arrayToSort, int start, int end){
        if (start < end){
            int positionPivot = split(arrayToSort, start, end);
            quickSort(arrayToSort, start, positionPivot -1);
            quickSort(arrayToSort, positionPivot+1, end);
        }
        return arrayToSort;
    }
    private int split (int[] arrayToSort, int startPosition, int endPosition){
        int pivot = arrayToSort[startPosition];
        int start = startPosition +1;
        int end = endPosition;
        int aux;

        while(start <= end){
            comparison++;
            if (arrayToSort[start]  <= pivot ){
                comparison++;
                start++;
            }else{
                if (pivot < arrayToSort[end]){
                    comparison++;
                    end--;
                }else{
                    aux = arrayToSort[start];
                    arrayToSort[start] = arrayToSort[end];
                    arrayToSort[end] = aux;
                    start++;
                    end--;
                    changes++;
                }
            }
        }
        arrayToSort[startPosition] = arrayToSort[end];
        arrayToSort[end] = pivot;
        changes++;
        return end;
    }

    public int[] sort(int[] arrayToSort){
        this.startTime();

        int[] returnArray = quickSort(arrayToSort,0,arrayToSort.length -1);

        this.printTime("QuickSort",changes,comparison,arrayToSort.length);

        return returnArray;
    }


}
