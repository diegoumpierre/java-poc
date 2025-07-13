package br.com.poc.brunoh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryOrganizeTest {


    @Test
    void organizePile(){

        LibraryOrganize libraryOrganize = new LibraryOrganize();

        int bookshelves = 2;
        int slots =2;

        String[] pile = new String[] {"Gado", "Diego", "List","mado", "uiego", "vist","bado", "xiego", "zist","sado", "aiego", "tist"};

        libraryOrganize.organizePile(bookshelves,slots,pile);

       /*
         * How many bookshelves?
         * How many slots?
         *
         * How many books?
         *
         * alphabetical order


        1. organize the array in alphabetical order
        2. calculate the possible slots
        3. get the array for the books leave in the pile
        4. interate the array and distributed in the bookshelves

         possibleSlots = slots * bookshelves;


         *
         */
    }


    @Test
    void organizePil2() {

        LibraryOrganize libraryOrganize = new LibraryOrganize();

        int bookshelves = 2;
        int slots = 2;

        String[] pile = new String[]{"The Lord of the Rings",
                "The Hitchhiker's Guide to the Galaxy",
                "Ready Player One",
                "Neuromancer",
                "The Foundation series"};

        libraryOrganize.organizePile(bookshelves, slots, pile);
    }


}