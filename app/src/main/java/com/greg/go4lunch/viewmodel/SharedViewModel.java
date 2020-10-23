package com.greg.go4lunch.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.greg.go4lunch.model.LikedRestaurant;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    public List<Restaurant> restaurants = new ArrayList<>();
    private MutableLiveData<ArrayList<Workmate>> workmates;
    private MutableLiveData<ArrayList<Workmate>> joiningWorkmates;
    private MutableLiveData<ArrayList<LikedRestaurant>> favorites;
    private MutableLiveData<ArrayList<Workmate>> pickedRestaurant;

    //----------------------------- Get all restaurants --------------------------------------------
    public List<Restaurant> getRestaurants(){ return restaurants; }

    //----------------------------- Get all workmates ----------------------------------------------
    public void initAllWorkmates(Context context){
        if (workmates != null){
            return;
        }
        workmates = Repository.getInstance(context).getAllWorkmates();
    }

    public LiveData<ArrayList<Workmate>> getAllWorkmatesData(){
        return workmates;
    }

    //----------------------------- Get joining workmates ------------------------------------------
    public void initJoiningWorkmates(Context context, String idPickedRestaurant){
        if (joiningWorkmates != null){
            return;
        }
        joiningWorkmates = Repository.getInstance(context).getJoiningWorkmates(idPickedRestaurant);
    }

    public LiveData<ArrayList<Workmate>> getJoiningWorkmatesData(){ return joiningWorkmates; }

    //----------------------------- Get favorite restaurant ----------------------------------------
    public void initFavoriteRestaurant(Context context, String uid, String idFavoriteRestaurant){
        if (favorites != null){
            return;
        }
        //6)
        favorites = Repository.getInstance(context).getFavoriteRestaurant(uid, idFavoriteRestaurant);

    }

    public LiveData<ArrayList<LikedRestaurant>> getFavoriteRestaurantData(){ return favorites; }

    //----------------------------- Get picked restaurant ------------------------------------------
    public void initPickedRestaurant(Context context, String uid, String idPickedRestaurant){
        if (pickedRestaurant != null){
            return;
        }
        pickedRestaurant = Repository.getInstance(context).getPickedRestaurant(uid, idPickedRestaurant);
    }

    public LiveData<ArrayList<Workmate>> getPickedRestaurantData(){ return pickedRestaurant; }
}