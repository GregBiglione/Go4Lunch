package com.greg.go4lunch.event;

import com.greg.go4lunch.model.Restaurant;

public class DetailedRestaurantEvent {

    public Restaurant restaurant;

    public DetailedRestaurantEvent(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
