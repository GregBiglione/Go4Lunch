package com.greg.go4lunch.model;

public class LikedRestaurant {
    private String uid;
    private String idRestaurant;
    private boolean favorite;

    // --- Empty constructor for Firebase ---
    public LikedRestaurant(){ }

    public LikedRestaurant(String uid, String idRestaurant, boolean favorite) {
        this.uid = uid;
        this.idRestaurant = idRestaurant;
        this.favorite = favorite;
    }

    // --- Getters ---
    public String getWorkmateId() { return uid; }
    public String getRestaurantId() { return idRestaurant; }
    public boolean getIsFavorite() { return favorite; }

    // --- Setters ---
    public void setWorkmateId(String workmateId) { this.uid = workmateId; }
    public void setRestaurantId(String restaurantId) { this.idRestaurant = restaurantId; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
