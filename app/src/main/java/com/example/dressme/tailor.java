package com.example.dressme;

import android.net.Uri;

public class tailor {
    private String imageUrl;
    private String fullname;
    private String username;
    private String userID;
    private String appear;

    public tailor() {
    }

    public tailor( String nomt,String usernameS,String Url, String utilID, String u) {
        fullname = nomt;
        username = usernameS;
        imageUrl = Url;
        userID = utilID;
        appear= u;

    }

    public String getAppear() {
        return appear;
    }

    public void setAppear(String appear) {
        this.appear = appear;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
