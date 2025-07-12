package br.com.dojo.algoritms;

import java.util.HashSet;
import java.util.Set;

public class Solution {

    private Set passedValues = new HashSet();
    public boolean findHappyNumber(int n){
        int currentNumber = n;
        passedValues.add(n);

        while (currentNumber != 1){
            currentNumber = squareNumberEspecial(currentNumber);
            if (passedValues.contains(currentNumber)) return false;
            passedValues.add(currentNumber);
        }
        return true;
    }

    public int squareNumberEspecial(int number){
        String stringNumber = String.valueOf(number);
        char[] strNumber = stringNumber.toCharArray();
        int sum = 0;

        for (int i=0; i < strNumber.length;i++){
            sum += Character.getNumericValue(strNumber[i]) * Character.getNumericValue(strNumber[i]);
        }
        return sum;
    }
}