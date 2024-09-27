package com.example.datvexemphim.ui.admin.model;

public class Combo {
    private String idCombo;
    private int giaCombo;
    private String moTa;
    private String tenCombo;
    private String anhCombo;

    public Combo() {
        // Default constructor required for calls to DataSnapshot.getValue(Combo.class)
    }

    public Combo(String idCombo, String tenCombo, int giaCombo, String moTa, String anhCombo) {
        this.idCombo = idCombo;
        this.giaCombo = giaCombo;
        this.moTa = moTa;
        this.tenCombo = tenCombo;
        this.anhCombo = anhCombo;
    }

    public String getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(String idCombo) {
        this.idCombo = idCombo;
    }

    public int getGiaCombo() {
        return giaCombo;
    }

    public void setGiaCombo(int giaCombo) {
        this.giaCombo = giaCombo;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTenCombo() {
        return tenCombo;
    }

    public void setTenCombo(String tenCombo) {
        this.tenCombo = tenCombo;
    }

    public String getAnhCombo() {
        return anhCombo;
    }

    public void setAnhCombo(String anhCombo) {
        this.anhCombo = anhCombo;
    }
}
