package com.greg.go4lunch.model;

import androidx.annotation.Nullable;

public class LikedRestaurant {
    private String uid;
    @Nullable private String idPickedRestaurant;
    private boolean favorite;

    //----------------------------- Empty constructor for Firebase ---------------------------------
    public LikedRestaurant(){ }

    public LikedRestaurant(String uid, String idPickedRestaurant, boolean favorite) {
        this.uid = uid;
        this.idPickedRestaurant = idPickedRestaurant;
        this.favorite = favorite;
    }

    //----------------------------- Getters --------------------------------------------------------
    public String getUid() { return uid; }
    @Nullable
    public String getIdPickedRestaurant() { return idPickedRestaurant; }
    public boolean isFavorite() { return favorite; }
}
