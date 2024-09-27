package com.example.datvexemphim.ui.admin.model;

public class loaiGhe {
    private String idKieuGhe;
    private String loai_ghe;
    private int giaGhe;

    public loaiGhe() {
    }

    public loaiGhe(String idKieuGhe, String loai_ghe, int giaGhe) {
        this.idKieuGhe = idKieuGhe;
        this.loai_ghe = loai_ghe;
        this.giaGhe = giaGhe;
    }

    public String getIdKieuGhe() {
        return idKieuGhe;
    }

    public void setIdKieuGhe(String idKieuGhe) {
        this.idKieuGhe = idKieuGhe;
    }

    public String getLoai_ghe() {
        return loai_ghe;
    }

    public void setLoai_ghe(String loai_ghe) {
        this.loai_ghe = loai_ghe;
    }

    public int getGiaGhe() {
        return giaGhe;
    }

    public void setGiaGhe(int giaGhe) {
        this.giaGhe = giaGhe;
    }
}
