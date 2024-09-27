package com.example.datvexemphim.ui.model;

public class ComboItem {
    private String idCombo;
    private String tenCombo;
    private String moTa;
    private String anhCombo;
    private int giaCombo;
    private int quantity;

    public ComboItem() {
        // Required default constructor for Firebase
    }

    public ComboItem(String idCombo, String tenCombo, String moTa, String anhCombo, int giaCombo, int quantity) {
        this.idCombo = idCombo;
        this.tenCombo = tenCombo;
        this.moTa = moTa;
        this.anhCombo = anhCombo;
        this.giaCombo = giaCombo;
        this.quantity = quantity;
    }

    public String getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(String idCombo) {
        this.idCombo = idCombo;
    }

    public String getTenCombo() {
        return tenCombo;
    }

    public void setTenCombo(String tenCombo) {
        this.tenCombo = tenCombo;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getAnhCombo() {
        return anhCombo;
    }

    public void setAnhCombo(String anhCombo) {
        this.anhCombo = anhCombo;
    }

    public int getGiaCombo() {
        return giaCombo;
    }

    public void setGiaCombo(int giaCombo) {
        this.giaCombo = giaCombo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
