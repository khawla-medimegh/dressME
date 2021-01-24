package com.example.dressme;



public class LikeModel {
    private String liker;
    private String tailor ;
    private String name;
    private String imageUrl;
    public LikeModel(){}

    public LikeModel(String liker, String tailor, String name) {
        this.liker = liker;
        this.tailor = tailor;
        this.name = name;
    }

    public String getLiker() {
        return liker;
    }

    public void setLiker(String liker) {
        this.liker = liker;
    }

    public String getTailor() {
        return tailor;
    }

    public void setTailor(String tailor) {
        this.tailor = tailor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
