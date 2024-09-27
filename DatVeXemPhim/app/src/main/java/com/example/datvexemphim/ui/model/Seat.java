package com.example.datvexemphim.ui.model;

public class Seat {
    private String loaiGhe;
    private int phongChieu;
    private String trangThai;
    private int vt_cot;
    private String vt_day;

    public Seat() {
        // Constructor mặc định dùng cho Firebase
    }

    public Seat(String loaiGhe, int phongChieu, String trangThai, int vt_cot, String vt_day) {
        this.loaiGhe = loaiGhe;
        this.phongChieu = phongChieu;
        this.trangThai = trangThai;
        this.vt_cot = vt_cot;
        this.vt_day = vt_day;
    }

    // Getters và setters (cần thiết nếu bạn muốn sử dụng)
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
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
}
