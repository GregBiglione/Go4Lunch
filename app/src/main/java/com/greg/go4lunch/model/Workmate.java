package com.greg.go4lunch.model;

import androidx.annotation.Nullable;

public class Workmate {
    private String uid;
    @Nullable private String picture;
    private String name;
    private String email;
    @Nullable private String pickedRestaurant;
    private boolean isJoining;

    public Workmate(){ }

    public Workmate(String uid, @Nullable String picture, String name, String email, @Nullable String pickedRestaurant, boolean isJoining) {
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

    @Nullable
    public String getPicture() {
        return picture;
    }

    public void setPicture(@Nullable String picture) {
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

    @Nullable
    public String getPickedRestaurant() {
        return pickedRestaurant;
    }

    public void setPickedRestaurant(@Nullable String pickedRestaurant) {
        this.pickedRestaurant = pickedRestaurant;
    }

    public boolean isJoining() {
        return isJoining;
    }

    public void setJoining(boolean joining) {
        isJoining = joining;
    }
}
