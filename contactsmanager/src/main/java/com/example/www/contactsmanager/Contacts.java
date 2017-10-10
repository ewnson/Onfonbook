package com.example.www.contactsmanager;

import com.google.gson.annotations.SerializedName;

public class Contacts {
    @SerializedName("cid")
    public int cid;
    @SerializedName("name")
    public String name;
    @SerializedName("pnumber")
    public String pnumber;
    @SerializedName("email")
    public String email;
    @SerializedName("count")
    private String count;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return pnumber;
    }

    public void setNumber(String number) {
        this.pnumber = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Contacts(){
    }
}
