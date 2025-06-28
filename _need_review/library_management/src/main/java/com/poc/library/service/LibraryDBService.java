package com.poc.library.service;

import com.poc.library.model.Book;
import com.poc.library.model.BookCategory;
import com.poc.library.model.BookSubCategory;
import com.poc.library.model.Person;
import com.poc.library.model.Rental;
import com.poc.library.model.RentalStatus;
import com.poc.library.model.Reservation;
import com.poc.library.model.ReservationStatus;
import com.poc.library.model.Shelf;
import lombok.Data;

import java.util.List;

@Data
public class LibraryDBService {
    private List<Book> bookList;
    private List<BookCategory> categoryList;
    private List<BookSubCategory> subCategoryList;
    private List<Person> personList;
    private List<Rental> rentalList;
    private List<Reservation> reservationList;
    private List<Shelf> shelfList;


    public void saveReservation(Reservation reservation) {

        reservationList.add(reservation);

    }

    public void updateReservation(Reservation reservation) {
        for(int i=0; i < reservationList.size();i++){
            Reservation reservationOld = reservationList.get(i);
            if (reservationOld.getAid().equals(reservation.getAid())){
                reservationList.set(i,reservation);
                break;
            }
        }



//        reservationList.remove(reservation.getAid())

    }

    public void saveRental(Rental rental) {
        rentalList.add(rental);
    }

    public void updateRental(Rental rental) {
        for(int i=0; i < rentalList.size();i++){
            Rental rentalOld = rentalList.get(i);
            if (rentalOld.getAid().equals(rental.getAid())){
                rentalList.set(i,rental);
                break;
            }
        }
    }
}