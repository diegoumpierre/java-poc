package com.poc;


/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class BirdWatcher {
    private final int[] birdsPerDay;
    private int today = -1;

    public BirdWatcher(int[] birdsPerDay) {
        this.birdsPerDay = birdsPerDay.clone();
        this.today = birdsPerDay.length-1;
    }

    public int[] getLastWeek() {
        return new int[]{0, 2, 5, 3, 7, 8, 4};
    }

    public int getToday() {
        return birdsPerDay[today];
    }

    public void incrementTodaysCount() {
        birdsPerDay[today] = getToday() + 1;
    }

    public boolean hasDayWithoutBirds() {
        for(int i=0;i<birdsPerDay.length;i++){
            if (0 ==birdsPerDay[i]) return true;
        }
        return false;
    }

    public int getCountForFirstDays(int numberOfDays) {
        int sum = 0;
        for(int i=0;i<numberOfDays && i <birdsPerDay.length;i++){
            sum += birdsPerDay[i];
        }
        return sum;
    }

    //Some days are busier that others. A busy day is one where five or more birds have visited your garden.
    public int getBusyDays() {
        int sum = 0;
        for(int i=0;i<birdsPerDay.length;i++){
            if (birdsPerDay[i] >= 5) sum++ ;
            else sum = sum;
        }
        return sum;
    }
}
