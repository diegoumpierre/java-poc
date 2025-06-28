package com.poc;




/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 27 2023
 */
public class Solution {

    int colorCode(String color) {
        String[] colorList = colors();
        for (int i=0;i<colorList.length;i++){
            if (color.equals(colorList[i])) return i;
        }
        return -1;
    }

    String[] colors() {
        String[] colorList = new String[10];
        colorList[0] = "black";
        colorList[1] = "brown";
        colorList[2] = "red";
        colorList[3] = "orange";
        colorList[4] = "yellow";
        colorList[5] = "green";
        colorList[6] = "blue";
        colorList[7] = "violet";
        colorList[8] = "grey";
        colorList[9] = "white";

        return colorList;
    }
}
