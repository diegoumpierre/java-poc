package com.poc;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class BirdWatcherTest {
    private BirdWatcher birdWatcher;

    @Test
    void getLastWeekShouldSuccess(){
        int[] birdsPerDayExpected = new int[]{0, 2, 5, 3, 7, 8, 4};
        birdWatcher = new BirdWatcher(new int[]{0, 2, 5, 3, 7, 8, 4});

        assertArrayEquals(birdsPerDayExpected, birdWatcher.getLastWeek());
    }

    @Test
    void howManyBirdsVisitTodayShouldBeSuccess(){
        int[] birdsPerDay = { 2, 5, 0, 7, 4, 1 };
        birdWatcher = new BirdWatcher(birdsPerDay);
        assertEquals(1,birdWatcher.getToday());
    }

    @Test
    void howManyBirdsVisitTodayShouldBeSuccess2(){
        int[] birdsPerDay = { 2, 5, 0, 7, 4, 1 };
        birdWatcher = new BirdWatcher(birdsPerDay);
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();

        assertEquals(5,birdWatcher.getToday());
    }

    @Test
    void howManyBirdsVisitTodayShouldBeSuccessResetWeek(){
        int[] birdsPerDay = { 2, 5, 0, 7, 4, 1 };
        birdWatcher = new BirdWatcher(birdsPerDay);
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();
        birdWatcher.incrementTodaysCount();

        assertEquals(7,birdWatcher.getToday());
    }


    @Test
    void dayWithOutBirdsShouldBeTrue() {
        int[] birdsPerDay = {2, 5, 0, 7, 4, 1};
        birdWatcher = new BirdWatcher(birdsPerDay);
        assertTrue(birdWatcher.hasDayWithoutBirds());
    }

    @Test
    void dayWithOutBirdsShouldBeFalse() {
        int[] birdsPerDay = {2, 5, 2, 7, 4, 1};
        birdWatcher = new BirdWatcher(birdsPerDay);
        assertFalse(birdWatcher.hasDayWithoutBirds());
    }

    @Test
    void countForFirstDayShouldSucess() {
        int[] birdsPerDay ={ 2, 5, 0, 7, 4, 1 };
        birdWatcher = new BirdWatcher(birdsPerDay);
        assertEquals(14,birdWatcher.getCountForFirstDays(4));
    }

    @Test
    void busyDaysShouldSucess() {
        int[] birdsPerDay ={ 2, 5, 0, 7, 4, 6 };
        birdWatcher = new BirdWatcher(birdsPerDay);
        assertEquals(3,birdWatcher.getBusyDays());
    }


}