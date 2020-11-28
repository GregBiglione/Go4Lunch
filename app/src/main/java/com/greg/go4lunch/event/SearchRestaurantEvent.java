package com.greg.go4lunch.event;

import com.greg.go4lunch.model.Restaurant;

public class SearchRestaurantEvent {

    public Restaurant restaurant;

    public SearchRestaurantEvent(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
