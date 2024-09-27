package com.example.datvexemphim.ui.admin.model;

public class PhongChieu {
    String idPhong;
    int soCot;
    int soDay;

    public PhongChieu() {
    }

    public PhongChieu(String idPhong, int soCot, int soDay) {
        this.idPhong = idPhong;
        this.soCot = soCot;
        this.soDay = soDay;
    }

    public String getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(String idPhong) {
        this.idPhong = idPhong;
    }

    public int getSoCot() {
        return soCot;
    }

    public void setSoCot(int soCot) {
        this.soCot = soCot;
    }

    public int getSoDay() {
        return soDay;
    }

    public void setSoDay(int soDay) {
        this.soDay = soDay;
    }
}
