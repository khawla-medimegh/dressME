package com.example.dressme;

public class customizeModel {
    private String imgUrl;
    private String Ntailor;
    private String tailID;
    private String Url1;
    private String Url2;
    private String Url3;


    public customizeModel() {
    }

    public customizeModel(String imgUrl, String ntailor, String tailID, String url1, String url2, String url3) {
        this.imgUrl = imgUrl;
        this.Ntailor = ntailor;
        this.tailID = tailID;
        this.Url1 = url1;
        this.Url2 = url2;
        this.Url3 = url3;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNtailor() {
        return Ntailor;
    }

    public void setNtailor(String ntailor) {
        Ntailor = ntailor;
    }

    public String getTailID() {
        return tailID;
    }

    public void setTailID(String tailID) {
        this.tailID = tailID;
    }

    public String getUrl1() {
        return Url1;
    }

    public void setUrl1(String url1) {
        Url1 = url1;
    }

    public String getUrl2() {
        return Url2;
    }

    public void setUrl2(String url2) {
        Url2 = url2;
    }

    public String getUrl3() {
        return Url3;
    }

    public void setUrl3(String url3) {
        Url3 = url3;
    }
}
