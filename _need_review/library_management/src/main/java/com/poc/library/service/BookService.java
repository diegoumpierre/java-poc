package com.poc.library.service;

import com.poc.library.model.Book;
import com.poc.library.model.Person;
import com.poc.library.model.Rental;
import com.poc.library.model.RentalStatus;
import com.poc.library.model.Reservation;
import com.poc.library.model.ReservationStatus;
import com.poc.library.service.messages.LibraryMessages;

import java.util.Date;

public class BookService {

    private LibraryDBService libraryDBService;

    public Rental borrowBook(Book book, Person person){
        Rental rental = getBookRental(book);
        if ( rental == null){
            Reservation reservation = getBookReservation(book);
            if (reservation != null){
                if (reservation.getPerson().getAid().equals(person.getAid())){
                    //create the rental
                    rental = new Rental(book, person);
                    libraryDBService.saveRental(rental);

                    //update the reservation
                    reservation.setEndDate(new Date());
                    reservation.setReservationStatus(ReservationStatus.INACTIVE);
                    libraryDBService.updateReservation(reservation);
                }else{
                    printLog(LibraryMessages.RESERVED_ANOTHER_PERSON);
                }
            }else{
                //create the rental
                rental = new Rental(book, person);
                libraryDBService.saveRental(rental);
            }
        }else{
            printLog(LibraryMessages.BOOK_NOT_AVALIABLE_BORROW);
        }
        return rental;
    }

    public void returnBook(Book book){
        Rental rental = getBookRental(book);
        rental.setEndDate(new Date());
        rental.setRentalStatus(RentalStatus.FINISHED);
        libraryDBService.updateRental(rental);
    }

    public void calculateLateFeeAmount(Book book){

    }


    public void madeBookReservation(Book book, Person person){
        Reservation reservation = getBookReservation(book);
        if (reservation != null){
            if (reservation.getPerson().getAid().equals(person.getAid())){
                printLog(LibraryMessages.BOOK_ALREADY_RESERVED_THESE_PERSON);
            }else{
                printLog(LibraryMessages.RESERVED_ANOTHER_PERSON);
            }
        }else{
            reservation = new Reservation(book,person);
            libraryDBService.saveReservation(reservation);
        }
    }


    public void updateReservationStatus(){

    }
    public void updateRentalStatus(){

    }

    public Reservation getBookReservation(Book book){
        for(Reservation reservation :libraryDBService.getReservationList()){
            if(reservation.getReservationStatus().equals(ReservationStatus.INACTIVE)) continue;

            if (reservation.getBook().getAid().equals(book.getAid())){
                if(reservation.getReservationStatus().equals(ReservationStatus.ACTIVE)) return reservation;
            }
        }
        return null;
    }
    public Rental getBookRental(Book book){
        for(Rental rental :libraryDBService.getRentalList()){
            if (rental.getRentalStatus().equals(RentalStatus.FINISHED)) continue;

            if (rental.getBook().getAid().equals(book.getAid())){
                if (rental.getRentalStatus().equals(RentalStatus.BORROWED) ||
                        rental.getRentalStatus().equals(RentalStatus.LATE)
                ) return rental;
            }
        }
        return null;
    }

    private void printLog(LibraryMessages libraryMessages){
        System.out.print(libraryMessages.getCode() +" - ");
        System.out.println(libraryMessages.getMsg());
    }

}