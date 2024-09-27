package com.example.datvexemphim.ui.admin.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String fullName;
    private String email;
    private String ngaySinh;
    private String soDienThoai;
    private String avatar;
    private String isUser;

    public User(){

    }

    public User(String fullName, String email, String ngaySinh, String soDienThoai, String avatar,  String isUser) {
        this.fullName = fullName;
        this.email = email;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.avatar = avatar;
        this.isUser = isUser;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("fullName", fullName);
        result.put("email", email);
        result.put("ngaySinh", ngaySinh);
        result.put("soDienThoai", soDienThoai);
        result.put("avatar", avatar);
        result.put("isUser", isUser);
        return result;
    }

}
