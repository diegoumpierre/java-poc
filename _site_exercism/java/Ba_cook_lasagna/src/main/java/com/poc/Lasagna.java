package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Lasagna {

    // TODO: define the 'expectedMinutesInOven()' method
    public int expectedMinutesInOven(){
        return 40;
    }

    // TODO: define the 'remainingMinutesInOven()' method
    public int remainingMinutesInOven(int passedMinutes){
        return expectedMinutesInOven() - passedMinutes;
    }

    // TODO: define the 'preparationTimeInMinutes()' method
    public int preparationTimeInMinutes(int qtdLayers){
        return qtdLayers*2;
    }

    // TODO: define the 'totalTimeInMinutes()' method
    public int totalTimeInMinutes(int qtdLayers, int passedMinutesInTheOven){
        int total = preparationTimeInMinutes(qtdLayers);
        total += passedMinutesInTheOven;
        return total;
    }
}
