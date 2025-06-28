package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class ElonsToyCar {

    private int distance = 0;
    private int battery = 100;


    public static ElonsToyCar buy() {
        return new ElonsToyCar();
    }

    public String distanceDisplay() {
        return "Driven "+distance+" meters";
    }

    public String batteryDisplay() {

        if (battery <= 0){
            return "Battery empty";
        }else {
            return "Battery at "+battery+"%";
        }
    }

    public void drive() {
        if (battery > 0){
            this.distance += 20;
            this.battery--;
        }

    }
}
