package com.poc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaxSystemApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(TaxSystemApplication.class, args);
    }


    /*
        => split by state, year or the both

        oriented to manutence
        self-contained - isolation
        lifecycle


        - state
            CONS
                - need if inside to check the year
                - all new year, need touch all the states files
            PRO
                - have all tax about that state in one place
        - year
            CONS
                - need if inside to check the state
            PRO
                - create just one file per year
                - when delete, just one file

    // what is the best to use (pro and cons)
    // by year the code is self-contained
    //what is the consequence of that



    */

}