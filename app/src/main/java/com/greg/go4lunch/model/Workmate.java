package com.greg.go4lunch.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;

public class Workmate {
    private String uid;
    @Nullable private String picture;
    private String name;
    private String email;
    @Nullable private String pickedRestaurant;
    @Nullable private String idPickedRestaurant;
    @Nullable private String addressRestaurant;
    //private PhotoMetadata photoRestaurant;
    private float ratingRestaurant;
    @Nullable private String websiteRestaurant;
    @Nullable private String phoneRestaurant;
    @Nullable private String distanceFromUser;
    //private LatLng latLng;
    private int joiningNumber;
    private int openingHour;
    private boolean joining;

    //----------------------------- Empty constructor for Firebase ---------------------------------
    public Workmate(){ }

    public Workmate(String uid, @Nullable String picture, String name, String email, @Nullable String pickedRestaurant,
                    @Nullable String idPickedRestaurant, @Nullable String addressRestaurant, /*PhotoMetadata photoRestaurant,*/
                    float ratingRestaurant, @Nullable String websiteRestaurant, @Nullable String phoneRestaurant, @Nullable String distanceFromUser,
                    /*LatLng latLng,*/ int joiningNumber, int openingHour, boolean joining) {
        this.uid = uid;
        this.picture = picture;
        this.name = name;
        this.email = email;
        this.pickedRestaurant = pickedRestaurant;
        this.idPickedRestaurant = idPickedRestaurant;
        this.addressRestaurant = addressRestaurant;
        //this.photoRestaurant = photoRestaurant;
        this.ratingRestaurant = ratingRestaurant;
        this.websiteRestaurant = websiteRestaurant;
        this.phoneRestaurant = phoneRestaurant;
        this.distanceFromUser = distanceFromUser;
        //this.latLng = latLng;
        this.joiningNumber = joiningNumber;
        this.openingHour = openingHour;
        this.joining = joining;
    }

    //----------------------------- Getters --------------------------------------------------------
    public String getUid() { return uid; }
    @Nullable
    public String getPicture() { return picture; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    @Nullable
    public String getPickedRestaurant() { return pickedRestaurant; }
    @Nullable
    public String getIdPickedRestaurant() { return idPickedRestaurant; }
    public boolean getJoining() { return joining; }
    @Nullable
    public String getAddressRestaurant() { return addressRestaurant; }
    //public PhotoMetadata getPhotoRestaurant() { return photoRestaurant; }
    public float getRatingRestaurant() { return ratingRestaurant; }
    @Nullable
    public String getWebsiteRestaurant() { return websiteRestaurant; }
    @Nullable
    public String getPhoneRestaurant() { return phoneRestaurant; }
    @Nullable public String getDistanceFromUser() { return distanceFromUser; }
    //public LatLng getLatLng() { return latLng; }
    public int getOpeningHour() { return openingHour; }
    public int getJoiningNumber() { return joiningNumber; }

    //----------------------------- Setters --------------------------------------------------------
    public void setUid(String uid) { this.uid = uid; }
    public void setPicture(@Nullable String picture) { this.picture = picture; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPickedRestaurant(@Nullable String pickedRestaurant) { this.pickedRestaurant = pickedRestaurant; }
    public void setIdPickedRestaurant(@Nullable String idPickedRestaurant) { this.idPickedRestaurant = idPickedRestaurant; }
    public void setJoining(boolean joining) { this.joining = joining; }
    public void setAddressRestaurant(@Nullable String addressRestaurant) { this.addressRestaurant = addressRestaurant; }
    //public void setPhotoRestaurant(@Nullable PhotoMetadata photoRestaurant) { this.photoRestaurant = photoRestaurant; }
    public void setRatingRestaurant(float ratingRestaurant) { this.ratingRestaurant = ratingRestaurant; }
    public void setWebsiteRestaurant(@Nullable String websiteRestaurant) { this.websiteRestaurant = websiteRestaurant; }
    public void setPhoneRestaurant(@Nullable String phoneRestaurant) { this.phoneRestaurant = phoneRestaurant; }
    public void setDistanceFromUser(@Nullable String distanceFromUser) { this.distanceFromUser = distanceFromUser; }
    //public void setLatLng(@Nullable LatLng latLng) { this.latLng = latLng; }
    public void setOpeningHour(int openingHour) { this.openingHour = openingHour; }
    public void setJoiningNumber(int joiningNumber) { this.joiningNumber = joiningNumber; }
}
