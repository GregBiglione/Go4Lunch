package com.greg.go4lunch.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.greg.go4lunch.model.Workmate;

import java.util.ArrayList;

public class WorkmateViewModel extends ViewModel {

    MutableLiveData<ArrayList<Workmate>> workmates;

    public void init(Context context){

    }

    public LiveData<ArrayList<Workmate>> getAllworkmates(){
        return workmates;
    }
}
