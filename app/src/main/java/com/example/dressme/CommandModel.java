package com.example.dressme;



public class CommandModel {
    private String comdUrl;
    private String  despComd ;
    private String tailorID ;
    private String price ;
    private String Ntailor ;
    private String etat;
    private String postIdUser;
    private String userID;
    private String Nclient;
    private String time;
    private String date;
    private String address;

    public CommandModel (){}


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNclient() {
        return Nclient;
    }

    public void setNclient(String nclient) {
        Nclient = nclient;
    }

    public String getPostIdUser() {
        return postIdUser;
    }

    public void setPostIdUser(String postIdUser) {
        this.postIdUser = postIdUser;
    }

    public CommandModel(String comdUrl, String despComd, String tailorID, String price, String ntailor, String etat, String postIdUser, String userID, String nclient, String time, String date, String address) {
        this.comdUrl = comdUrl;
        this.despComd = despComd;
        this.tailorID = tailorID;
        this.price = price;
        Ntailor = ntailor;
        this.etat = etat;
        this.postIdUser = postIdUser;
        this.userID = userID;
        Nclient = nclient;
        this.time = time;
        this.date = date;
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComdUrl() {
        return comdUrl;
    }

    public void setComdUrl(String comdUrl) {
        this.comdUrl = comdUrl;
    }

    public String getDespComd() {
        return despComd;
    }

    public void setDespComd(String despComd) {
        this.despComd = despComd;
    }

    public String getTailorID() {
        return tailorID;
    }

    public void setTailorID(String tailorID) {
        this.tailorID = tailorID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getNtailor(){
     return Ntailor;
    }

    public void setNtailor(String ntailor) {
        Ntailor = ntailor;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}
