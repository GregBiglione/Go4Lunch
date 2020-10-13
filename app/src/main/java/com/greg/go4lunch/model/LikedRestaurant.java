package com.greg.go4lunch.model;

import androidx.annotation.Nullable;

public class LikedRestaurant {
    private String uid;
    @Nullable private String idPickedRestaurant;
    private boolean favorite;

    // --- Empty constructor for FireBase ---
    public LikedRestaurant(){ }

    public LikedRestaurant(String uid, String idPickedRestaurant, boolean favorite) {
        this.uid = uid;
        this.idPickedRestaurant = idPickedRestaurant;
        this.favorite = favorite;
    }

    // --- Getters ---
    public String getWorkmateId() { return uid; }
    public String getRestaurantId() { return idPickedRestaurant; }
    public boolean getIsFavorite() { return favorite; }

    // --- Setters ---
    public void setWorkmateId(String workmateId) { this.uid = workmateId; }
    public void setRestaurantId(String restaurantId) { this.idPickedRestaurant = restaurantId; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
