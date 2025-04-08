package com.poc.ticketsystem.model;

public class Seat {
    private int id;
    private String number;
    private boolean isAvailable;


    public Seat(int id, String number){
        this.id = id;
        this.number = number;
        this.isAvailable = true;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

}
