package com.poc.ticketsystem.service;

import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.Venue;
import com.poc.ticketsystem.model.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    @BeforeEach
    void init(){
    }

    private List<Seat> createSeat(int startAt, int seatNumbers){
        List<Seat> seatList = new ArrayList<>();
            for (int i = startAt; i < seatNumbers; i++) {
            seatList.add(new Seat(i, String.valueOf(i+10)));
        }
        return seatList;
    }


    @Test
    void createAnEventShouldBeSuccess(){

        Zone zone1 = new Zone();
        zone1.setId(1001);
        zone1.setName("Zone 1");
        zone1.setCapacity(10);
        zone1.setSeats(createSeat(0,10));



        Venue venue = new Venue();
        venue.setId(1);
        venue.setName("Rock in A");
        venue.setCapacity(100);
        venue.setAddress("Some St, 100");
        venue.setZones(Collections.singletonList(zone1));










    }


}