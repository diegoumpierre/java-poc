package com.poc.ticketsystem.service;

import com.poc.ticketsystem.model.Event;
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

        //zones
        Zone zone1 = new Zone();
        zone1.setId(1001);
        zone1.setName("Zone 1");
        zone1.setCapacity(10);
        zone1.setSeats(createSeat(0,10));


        Zone zone2 = new Zone();
        zone1.setId(2002);
        zone1.setName("Zone 2");
        zone1.setCapacity(50);
        zone1.setSeats(createSeat(10,50));

        List<Zone> zoneList = new ArrayList<>();
        zoneList.add(zone1);
        zoneList.add(zone2);


        //Venue
        Venue venue = new Venue();
        venue.setId(1);
        venue.setName("Rock in A");
        venue.setCapacity(100);
        venue.setAddress("Some St, 100");

        //Event
        Event event = new Event();
        event.setId(1);
        event.setName("Nem Event Rock");
        event.setZones(zoneList);
        event.setVenue(venue);

        //now I have the event
        assertEquals(100, event.getVenue().getCapacity());



    }


}