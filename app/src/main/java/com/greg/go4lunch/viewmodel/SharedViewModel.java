package com.greg.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.greg.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public List<Restaurant> restaurants = new ArrayList<>();

    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public List<Restaurant> getRestaurants(){ return restaurants; }
}