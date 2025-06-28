package com.poc.library.model;

import lombok.Data;
import java.util.Date;
import java.util.UUID;

@Data
public class Reservation {
    private String aid;
    private Date startDate;
    private Date endDate;
    private Book book;
    private Person person;
    private ReservationStatus reservationStatus;

    public Reservation(Book book, Person person) {
        this.book = book;
        this.person = person;
        this.aid = UUID.randomUUID().toString();
        this.startDate = new Date();
        this.reservationStatus = ReservationStatus.ACTIVE;
    }
}
