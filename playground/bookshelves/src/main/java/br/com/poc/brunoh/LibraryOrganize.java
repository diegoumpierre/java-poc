package br.com.poc.brunoh;

import java.util.Arrays;
import java.util.HashMap;

public class LibraryOrganize {

    public void organizePile(int bookshelves, int slots, String[] pile){

        Arrays.sort(pile);

        int possibleSlots = bookshelves * slots;
        int maxStayInThePile = pile.length - possibleSlots;

        String[] stayInThePile =  new String[maxStayInThePile];
        String[][] finalBookshelve = new String[bookshelves][slots];

        int countSlot = 0;
        int countBookshelves = 0;
        int countStayInThePile = 0;

        for(int i=0;i < pile.length;i++) {

            if (i < possibleSlots) {

                if (countSlot == slots) {
                    countSlot = 0;
                    countBookshelves++;
                }
                finalBookshelve[countBookshelves][countSlot] = pile[i];
                countSlot++;

            } else {
                //add to the stay in the pile
                stayInThePile[countStayInThePile] = pile[i];
                countStayInThePile++;

            }
        }

        System.out.println(Arrays.toString(stayInThePile));

        System.out.println(Arrays.toString(finalBookshelve));


    }


}
       /*
        1. organize the array in alphabetical order
        2. calculate the possible slots
        3. get the array for the books leave in the pile
        4. interate the array and distributed in the bookshelves

         possibleSlots = slots * bookshelves;
         * How many bookshelves?
         * How many slots?
         *
         * How many books?
         *
         * alphabetical order
         *
         */