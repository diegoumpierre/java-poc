package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Jan 3 2024
 */
public class Leap {
    boolean isLeapYear(int year) {
        int mod = year % 4;

        if (mod == 0) {
            int mod100 = year % 100;
            int mod400 = year % 400;

            if (mod100 > 0) return true;

            if (mod400 > 0 || mod100 > 0) return false;

            return true;
        }
        return false;
    }
}
