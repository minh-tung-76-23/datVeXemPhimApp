package com.example.datvexemphim.ui.model;

public class Seat {
    private String id;
    private String loaiGhe;
    private int phongChieu;
    private String tinhTrang;
    private int vt_cot;
    private String vt_day;

    public Seat() {
        // Required default constructor for Firebase
    }

    public Seat(String id, String loaiGhe, int phongChieu, String tinhTrang, int vt_cot, String vt_day) {
        this.id = id;
        this.loaiGhe = loaiGhe;
        this.phongChieu = phongChieu;
        this.tinhTrang = tinhTrang;
        this.vt_cot = vt_cot;
        this.vt_day = vt_day;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(String loaiGhe) {
        this.loaiGhe = loaiGhe;
    }

    public int getPhongChieu() {
        return phongChieu;
    }

    public void setPhongChieu(int phongChieu) {
        this.phongChieu = phongChieu;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public int getVt_cot() {
        return vt_cot;
    }

    public void setVt_cot(int vt_cot) {
        this.vt_cot = vt_cot;
    }

    public String getVt_day() {
        return vt_day;
    }

    public void setVt_day(String vt_day) {
        this.vt_day = vt_day;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id='" + id + '\'' +
                ", loaiGhe='" + loaiGhe + '\'' +
                ", phongChieu=" + phongChieu +
                ", trangThai='" + tinhTrang + '\'' +
                ", vt_cot=" + vt_cot +
                ", vt_day='" + vt_day + '\'' +
                '}';
    }
}
