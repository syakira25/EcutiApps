package com.example.jameedean.ecutiapps.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Staff {

    private String name;
    private String password, phone, email;
    private String Uid;

    public Staff() {
        // This is default constructor.
    }

    public Staff(String name, String email, String password, String phone, String uid) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.Uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }
}