package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Solution {
    private int carSpeed = 221;

    public double productionRatePerHour(int speed) {

        int carProduce = carSpeed * speed;
        double ratePercentage = 1.0;

        if (speed >4 && speed <9 ) ratePercentage = 0.9;
        if (speed == 9 ) ratePercentage = 0.8;
        if (speed == 10 ) ratePercentage = 0.77;

        return carProduce * ratePercentage;

    }

    public int workingItemsPerMinute(int speed) {
        double productionPerHour = productionRatePerHour(speed);
        return (int) productionPerHour / 60;
    }
}
