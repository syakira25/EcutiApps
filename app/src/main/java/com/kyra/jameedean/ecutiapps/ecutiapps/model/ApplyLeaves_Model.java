package com.kyra.jameedean.ecutiapps.ecutiapps.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ApplyLeaves_Model{

    private String name;
    private String email;
    private String types_leave;
    private String date_start;
    private String date_end;
    private String message;
    private String total;
    private String uid;
    private String status;

    public ApplyLeaves_Model() {
        // This is default constructor.
    }

    public ApplyLeaves_Model(String name, String email, String types_leave, String date_start,String date_end, String message, String total, String uid){
        this.name = name;
        this.email = email;
        this.types_leave = types_leave;
        this.date_start = date_start;
        this.date_end = date_end;
        this.message = message;
        this.total = total;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email;}

    public void setEmail(String email){this.email = email;}

    public String getTypes_leave(){return types_leave;}

    public void setTypes_leave(String types_leave){this.types_leave=types_leave;}

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start= date_start;
    }

    public String getDate_end(){return date_end;}

    public void setDate_end(String date_end){this.date_end=date_end;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotal(){return total;}

    public void setTotal(String total_leave){this.total=total;}

    public  String getStatus(){return status;}

    public void setStatus(String status){this.status=status;}

    public String getUid(){return uid;}

    public void setUid(String uid){this.uid=uid;}

}
