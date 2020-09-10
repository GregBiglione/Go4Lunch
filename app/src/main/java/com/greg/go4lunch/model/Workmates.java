package com.greg.go4lunch.model;

public class Workmates {
    public String uid;
    public String picture;
    public String name;
    public String email;
    public boolean pickedRestaurant;

    public Workmates(String uid, String picture, String name, String email, boolean pickedRestaurant) {
        this.uid = uid;
        this.picture = picture;
        this.name = name;
        this.email = email;
        this.pickedRestaurant = pickedRestaurant;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public boolean isPickedRestaurant() {
        return pickedRestaurant;
    }

    public void setPickedRestaurant(boolean pickedRestaurant) {
        this.pickedRestaurant = pickedRestaurant;
    }
}
