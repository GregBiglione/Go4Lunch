package com.greg.go4lunch.model;

public class Workmate {
    public String uid;
    public String picture;
    public String name;
    public String email;
    public String pickedRestaurant;
    public boolean isJoining;

    public Workmate(String uid, String picture, String name, String email, String pickedRestaurant, boolean isJoining) {
        this.uid = uid;
        this.picture = picture;
        this.name = name;
        this.email = email;
        this.pickedRestaurant = pickedRestaurant;
        this.isJoining = isJoining;
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

    public String getPickedRestaurant() {
        return pickedRestaurant;
    }

    public void setPickedRestaurant(String pickedRestaurant) {
        this.pickedRestaurant = pickedRestaurant;
    }

    public boolean isJoining() {
        return isJoining;
    }

    public void setJoining(boolean joining) {
        isJoining = joining;
    }
}
