package com.example.datvexemphim.ui.admin.model;

public class GheNgoi {
    String id;
    String loaiGhe;
    String tinhTrang;
    int phongChieu;
    int vt_cot;
    String vt_day;

    public GheNgoi() {
    }

    public GheNgoi(String id, String loaiGhe, String tinhTrang, int phongChieu, int vt_cot, String vt_day) {
        this.id = id;
        this.loaiGhe = loaiGhe;
        this.tinhTrang = tinhTrang;
        this.phongChieu = phongChieu;
        this.vt_cot = vt_cot;
        this.vt_day = vt_day;
    }

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

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public int getPhongChieu() {
        return phongChieu;
    }

    public void setPhongChieu(int phongChieu) {
        this.phongChieu = phongChieu;
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

