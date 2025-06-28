package com.poc.library.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Data
public class Rental {
    private String aid;
    private Date startDate;
    private Date expectedEndDate;
    private Date endDate;

    private Book book;
    private Person person;
    private RentalStatus rentalStatus;

    private Double lateFeeAmount = 0.0;
    private Boolean lateFeePaid = Boolean.FALSE;

    public Rental(Book book, Person person) {

        this.book = book;
        this.person = person;
        this.aid = UUID.randomUUID().toString();
        this.startDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, book.getDaysForRental());
        this.expectedEndDate = c.getTime();
        this.rentalStatus = RentalStatus.BORROWED;
    }
}
