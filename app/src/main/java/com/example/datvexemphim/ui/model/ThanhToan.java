package com.example.datvexemphim.ui.model;

import java.util.List;

public class ThanhToan {
    private String userId;
    private String userEmail;
    private String nameFilm;
    private String cinemaName;
    private String cinemaAddress;
    private String timeDate;
    private String selectedTime;
    private String selectedSeats;
    private String totalPrice;
    private String selectedCombo;

    public ThanhToan() {
        // Required default constructor for Firebase
    }

    public ThanhToan(String userId, String userEmail, String nameFilm, String cinemaName, String cinemaAddress, String timeDate, String selectedTime, String selectedSeats, String totalPrice, String selectedCombo) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.nameFilm = nameFilm;
        this.cinemaName = cinemaName;
        this.cinemaAddress = cinemaAddress;
        this.timeDate = timeDate;
        this.selectedTime = selectedTime;
        this.selectedSeats = selectedSeats;
        this.totalPrice = totalPrice;
        this.selectedCombo = selectedCombo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getNameFilm() {
        return nameFilm;
    }

    public void setNameFilm(String nameFilm) {
        this.nameFilm = nameFilm;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public String getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSelectedCombo() {
        return selectedCombo;
    }

    public void setSelectedCombo(String selectedCombo) {
        this.selectedCombo = selectedCombo;
    }
}
