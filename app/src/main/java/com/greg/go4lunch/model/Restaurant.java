package com.greg.go4lunch.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;

import org.parceler.Parcel;


@Parcel
public class Restaurant {
    private String idRestaurant, name, distanceFromUser, address, openingHour, phoneNumber, website;
    private PhotoMetadata restaurantPicture;
    private LatLng latLng;
    private int joiningNumber;
    private float rating;
    //private List<Workmate> joiningWorkmate;

    public Restaurant(){}

    // --- Getters ---
    public String getIdRestaurant() { return idRestaurant; }
    public String getName() { return name; }
    public String getDistanceFromUser() { return distanceFromUser; }
    public String getAddress() { return address; }
    public String getOpeningHour() { return openingHour; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getWebsite() { return website; }
    public PhotoMetadata getRestaurantPicture() { return restaurantPicture; }
    public LatLng getLatLng() { return latLng; }
    public int getJoiningNumber() { return joiningNumber; }
    public float getRating() { return rating; }

    // --- Setters ---
    public void setIdRestaurant(String idRestaurant) { this.idRestaurant = idRestaurant; }
    public void setName(String name) { this.name = name; }
    public void setDistanceFromUser(String distanceFromUser) { this.distanceFromUser = distanceFromUser; }
    public void setAddress(String address) { this.address = address; }
    public void setOpeningHour(String openingHour) { this.openingHour = openingHour; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setWebsite(String website) { this.website = website; }
    public void setRestaurantPicture(PhotoMetadata restaurantPicture) { this.restaurantPicture = restaurantPicture; }
    public void setLatLng(LatLng latLng) { this.latLng = latLng; }
    public void setJoiningNumber(int joiningNumber) { this.joiningNumber = joiningNumber; }
    public void setRating(float rating) { this.rating = rating; }
}
