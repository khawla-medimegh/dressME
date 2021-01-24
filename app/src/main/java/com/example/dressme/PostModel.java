package com.example.dressme;


public class PostModel {

    private String  description;
    private String postimage;
    private String publisher;
    public PostModel(){
    }

    public PostModel( String des, String Url, String pub){
        this.description= des;
        this.postimage = Url;
        this.publisher = pub;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPostimage(){return postimage; }
    public String getDescription(){return description;}
    public String getPublisher(){return publisher;}

}