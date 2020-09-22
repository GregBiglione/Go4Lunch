package com.greg.go4lunch.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Restaurant {
    private String idRestaurant, name, distanceFromUser, address, openingHour, phoneNumber, website;
    //private String name;
    //private String distanceFromUser;
    private LatLng latLng;
    Bitmap restaurantPicture;
    //private String address;
    private int joiningNumber;
    //private String openingHour;
    private float rating;
    //private String phoneNumber;
    //private String website;
    private List<Workmate> joiningWorkmate;

    public Restaurant(){}


    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Bitmap getRestaurantPicture() {
        return restaurantPicture;
    }

    public void setRestaurantPicture(Bitmap restaurantPicture) {
        this.restaurantPicture = restaurantPicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(String distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getJoiningNumber() {
        return joiningNumber;
    }

    public void setJoiningNumber(int joiningNumber) {
        this.joiningNumber = joiningNumber;
    }

    public String getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<Workmate> getJoiningWorkmate() {
        return joiningWorkmate;
    }

    public void setJoiningWorkmate(List<Workmate> joiningWorkmate) {
        this.joiningWorkmate = joiningWorkmate;
    }
}
