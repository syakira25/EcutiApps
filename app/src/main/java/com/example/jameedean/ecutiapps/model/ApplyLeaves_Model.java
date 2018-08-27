package com.example.jameedean.ecutiapps.model;

import java.io.Serializable;
import java.util.HashMap;

public class ApplyLeaves_Model implements Serializable{

    private String name;
    private String message;
    private String date;

    public ApplyLeaves_Model() {
        // This is default constructor.
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
