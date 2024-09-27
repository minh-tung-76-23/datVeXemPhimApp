package com.example.datvexemphim.ui.admin.model;

public class Phong {
    private String idPhong;
    private int soDay;
    private int soCot;

    // Default constructor (needed for Firebase)
    public Phong() {
    }

    // Constructor with parameters
    public Phong(String idPhong, int soDay, int soCot) {
        this.idPhong = idPhong;
        this.soDay = soDay;
        this.soCot = soCot;
    }

    // Getters and Setters
    public String getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(String idPhong) {
        this.idPhong = idPhong;
    }

    public int getsoDay() {
        return soDay;
    }

    public void setsoDay(int soDay) {
        this.soDay = soDay;
    }

    public int getsoCot() {
        return soCot;
    }

    public void setsoCot(int soCot) {
        this.soCot = soCot;
    }
}
