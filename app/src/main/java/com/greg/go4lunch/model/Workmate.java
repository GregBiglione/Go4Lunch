package com.greg.go4lunch.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;

public class Workmate {
    private String uid;
    @Nullable private String picture;
    private String name;
    private String email;
    private String pickedRestaurant;
    private String idPickedRestaurant;
    private String addressRestaurant;
    private boolean joining;

    //----------------------------- Empty constructor for Firebase ---------------------------------
    public Workmate(){ }

    public Workmate(String uid, @Nullable String picture, String name, String email, String pickedRestaurant,
                    String idPickedRestaurant, String addressRestaurant, boolean joining) {
        this.uid = uid;
        this.picture = picture;
        this.name = name;
        this.email = email;
        this.pickedRestaurant = pickedRestaurant;
        this.idPickedRestaurant = idPickedRestaurant;
        this.addressRestaurant = addressRestaurant;
        this.joining = joining;
    }

    //----------------------------- Getters --------------------------------------------------------
    public String getUid() { return uid; }
    @Nullable public String getPicture() { return picture; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPickedRestaurant() { return pickedRestaurant; }
    public String getIdPickedRestaurant() { return idPickedRestaurant; }
    public String getAddressRestaurant() { return addressRestaurant; }
    public boolean getJoining() { return joining; }

    //----------------------------- Setters --------------------------------------------------------
    public void setUid(String uid) { this.uid = uid; }
    public void setPicture(@Nullable String picture) { this.picture = picture; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPickedRestaurant(String pickedRestaurant) { this.pickedRestaurant = pickedRestaurant; }
    public void setIdPickedRestaurant(String idPickedRestaurant) { this.idPickedRestaurant = idPickedRestaurant; }
    public void setAddressRestaurant(String addressRestaurant) { this.addressRestaurant = addressRestaurant; }
    public void setJoining(boolean joining) { this.joining = joining; }
}
