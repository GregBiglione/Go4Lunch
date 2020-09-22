package com.greg.go4lunch.repository;

import androidx.lifecycle.MutableLiveData;

import com.greg.go4lunch.model.Workmate;

import java.util.ArrayList;

public class Repository {

    static Repository instance;
    private ArrayList<Workmate> workmates;

    public static Repository getInstance(){
        if (instance == null){
            new Repository();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<Workmate>> getAllWorkmates(){
        loadWorkmates();
        MutableLiveData<ArrayList<Workmate>> coWorkers = new MutableLiveData<>();
        coWorkers.setValue(workmates);

        return coWorkers;
    }

    private void loadWorkmates() {
    }
}
