package com.example.assessment04;

public class Seat {
    private int seatNumber;
    private boolean isBooked;
    private boolean isUserSeat;
    private String cellNumber = null;
    private boolean isSeat = true;

    private boolean isSelected = false;

    public String getCellNumber() {
        return cellNumber;
    }

    public Seat(int seatNumber, boolean isBooked, boolean isUserSeat) {
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
        this.isUserSeat = isUserSeat;
    }

    public Seat(String cellNumber) {
        this.cellNumber = cellNumber;
        isSeat = false;
    }

    public boolean isSeat() {
        return isSeat;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public boolean isUserSeat() {
        return isUserSeat;
    }

    public void setUserSeat(boolean userSeat) {
        isUserSeat = userSeat;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
