package com.greg.go4lunch.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.greg.go4lunch.api.WorkmateHelper;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    public List<Restaurant> restaurants = new ArrayList<>();
    MutableLiveData<ArrayList<Workmate>> workmates;

    public List<Restaurant> getRestaurants(){ return restaurants; }

    public void init(Context context){
        if (workmates != null){
            return;
        }
        workmates = Repository.getInstance(context).getAllWorkmates();
    }

    public LiveData<ArrayList<Workmate>> getAllWorkmatesData(){
        return workmates;
    }
}