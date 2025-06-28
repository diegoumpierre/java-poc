package com.poc;

import static java.lang.Math.sqrt;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 26 2023
 */
public class Solution {

/*
If the dart lands outside the target, player earns no points (0 points).
If the dart lands in the outer circle of the target, player earns 1 point.
If the dart lands in the middle circle of the target, player earns 5 points.
If the dart lands in the inner circle of the target, player earns 10 points.
The outer circle has a radius of 10 units (this is equivalent to the total radius for the entire target),
the middle circle a radius of 5 units, and the inner circle a radius of 1.
 */

    private int outerCircle = 10;
    private int middleCircle = 5;
    private int innerCircle = 1;

    public int darts(double x, double y){
        double distance = sqrt((x*x) + (y*y));

        if (distance > middleCircle && distance <= outerCircle) return 1;

        if (distance > innerCircle && distance <= middleCircle) return 5;

        if (distance >= 0 && distance <= innerCircle) return 10;

        return 0;
    }

}
/*
    1. indicate if is a 501 or 301 game
    2. pass two arrays bi dimensional with the players moves,
    in each position have 3 position where the dart arrive
    3. return an array with the final score for each player finalScore[0]playerOne, finalScore[0]playerTwo
 */