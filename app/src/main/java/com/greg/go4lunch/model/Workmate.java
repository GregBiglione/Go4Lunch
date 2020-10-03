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

    // --- Getters ---
    public String getUid() { return uid; }
    @Nullable
    public String getPicture() { return picture; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    @Nullable
    public String getPickedRestaurant() { return pickedRestaurant; }
    public boolean isJoining() { return isJoining; }

    // --- Setters ---
    public void setUid(String uid) { this.uid = uid; }
    public void setPicture(@Nullable String picture) { this.picture = picture; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPickedRestaurant(@Nullable String pickedRestaurant) { this.pickedRestaurant = pickedRestaurant; }
    public void setIsJoining(boolean isJoining) { isJoining = isJoining; }
}
