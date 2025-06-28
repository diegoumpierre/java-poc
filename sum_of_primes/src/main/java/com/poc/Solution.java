package com.poc;

import java.util.ArrayList;
import java.util.List;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 18 2023
 */
public class Solution {

    List<Integer> primeNumbers = new ArrayList<>();

    /**
     * maior que um
     * divisivel por ele mesmo
     * Se o resto da divisão for zero o número não é primo.
     * Se nenhum resto for zero, o número é primo
     *
     * @param number
     * @return
     */
    public boolean isPrime(int number){
        for (int i=2; i < number;i++){
            if ((number%i) == 0) return false;
        }
        return true;
    }


    private void loadPrimeNumbers(int p){
        for(int i=2; i <= p; i++){
            if (isPrime(i)){
                primeNumbers.add(i);
            }
        }
    }

    private int findPrimeForP(int actualP){
        actualP -= 1;
        while (!isPrime(actualP)){
            actualP -= 1;
        }
        return actualP;
    }


    public int findSolution(int n){
        int p = findPrimeForP(n);
        loadPrimeNumbers(p);

        while(!isConsecutiveSum(p)){
            p = findPrimeForP(p);
        }
        return p;
    }

    private boolean isConsecutiveSum(int p){
        int sum = 0;
        int left = 0;
        int right = 0;
        int max = primeNumbers.size()-1;

        while (sum < p && right < max && left <= right){
            sum += primeNumbers.get(right);

            if (p == sum) return true;

            if (sum < p ) right++;
            if (sum > p ){
                sum -= primeNumbers.get(left);
                sum -= primeNumbers.get(right);
                left++;
            }
        }
        return false;
    }

}
