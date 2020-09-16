package com.greg.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.List;

public class Restaurant {
    private String name;
    private String distanceFromUser;
    @Nullable String restaurantPicture;
    private String address;
    private int joiningNumber;
    private String openingHour;
    private float rating;
    private String phoneNumber;
    private String website;
    private List<Workmate> joiningWorkmate;

    public Restaurant(){}

    public Restaurant(String name, String distanceFromUser, @Nullable String restaurantPicture,
                      String address, int joiningNumber, String openingHour, float rating,
                      String phoneNumber, String website, List<Workmate> joiningWorkmate) {
        this.name = name;
        this.distanceFromUser = distanceFromUser;
        this.restaurantPicture = restaurantPicture;
        this.address = address;
        this.joiningNumber = joiningNumber;
        this.openingHour = openingHour;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.joiningWorkmate = joiningWorkmate;
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

    @Nullable
    public String getRestaurantPicture() {
        return restaurantPicture;
    }

    public void setRestaurantPicture(@Nullable String restaurantPicture) {
        this.restaurantPicture = restaurantPicture;
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
