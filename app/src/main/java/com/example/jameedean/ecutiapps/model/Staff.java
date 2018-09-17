package com.example.jameedean.ecutiapps.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Staff {

    public  String name, job_position, role;
    private String password, phone, email;
    private String annual, mc, el, public_leave;
    private String Uid;

    public Staff() {
        // This is default constructor.
    }

    public Staff(String name, String email, String password, String phone, String job_position,String uid, String annual, String mc, String el, String public_leave, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.job_position  = job_position;
        this.Uid = uid;
        this.annual = annual;
        this.mc = mc;
        this.el = el;
        this.public_leave = public_leave;
        this.role = role;
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

    public String getJob_position(){return job_position;}

    public void setJob_position(String job_position){this.job_position=job_position;}

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public  String getAnnual(){return annual;}

    public  void setAnnual(String annual){this.annual=annual;}

    public String getMc(){return mc;}

    public void setMc(String mc){this.mc=mc;}

    public String getEl(){return el;}

    public void setEl(String el){this.el=el;}

    public String getPublic_leave(){return public_leave;}

    public void setPublic_leave(String public_leave){this.public_leave=public_leave;}

    public String getRole(){return role;}

    public void setRole(String role){this.role=role;}

}