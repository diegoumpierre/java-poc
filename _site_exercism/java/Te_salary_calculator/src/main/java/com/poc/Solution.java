package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Oct 09 2023
 */
public class Solution {

    public double multiplierPerDaysSkipped(int daysSkipped) {
        return daysSkipped < 5 ? 1: 0.85d;
    }

    public int multiplierPerProductsSold(int productsSold) {
        return productsSold > 20 ? 13: 10;
    }

    public double bonusForProductSold(int productsSold) {
        return productsSold * multiplierPerProductsSold(productsSold);
    }

    public double finalSalary(int daysSkipped, int productsSold) {
        double baseSalary = 1000.00;
        double maxSalary = 2000.00;
        double salary = baseSalary *multiplierPerDaysSkipped(daysSkipped);
        salary += bonusForProductSold(productsSold);

        return  salary > maxSalary? maxSalary:salary;
    }



}
