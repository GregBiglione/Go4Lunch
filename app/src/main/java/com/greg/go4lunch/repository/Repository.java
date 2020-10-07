package com.greg.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.greg.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    static Repository instance;
    private ArrayList<Workmate> workmates = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Repository";

    //private ArrayList<Workmate> joiningWorkmates = new ArrayList<>();

    public static Repository getInstance(Context context){
        if (instance == null){
            instance = new Repository();
        }
        return instance;
    }

    // ---------------------------- Get all workmates ----------------------------------------------
    public MutableLiveData<ArrayList<Workmate>> getAllWorkmates(){
        loadWorkmates();
        MutableLiveData<ArrayList<Workmate>> allWorkmates = new MutableLiveData<>();
        allWorkmates.setValue(workmates);
        return allWorkmates;
    }

    private void loadWorkmates() {
        db.collection("workmates")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : list) {
                        workmates.add(documentSnapshot.toObject(Workmate.class));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Impossible to get workmates list", e);
            }
        });
    }

    // ---------------------------- Get joining workmates ------------------------------------------
    //public MutableLiveData<ArrayList<Workmate>> getJoiningWorkmates(){
    //    loadJoiningWorkmates();
    //    MutableLiveData<ArrayList<Workmate>> allJoiningWorkmates = new MutableLiveData<>();
    //    allJoiningWorkmates.setValue(joiningWorkmates);
    //    return allJoiningWorkmates;
    //}
//
    ////Créer list avec id restaurant et id workmates ??
    //private void loadJoiningWorkmates() {
    //    db.collection("workmates")
    //            .whereEqualTo("pickedRestaurant", joiningWorkmates.get(0).getPickedRestaurant())
    //            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
    //        @Override
    //        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
    //            if (!queryDocumentSnapshots.isEmpty()){
    //                List<DocumentSnapshot> joiningList = queryDocumentSnapshots.getDocuments();
    //                for ( DocumentSnapshot documentSnapshot : joiningList) {
    //                    joiningWorkmates.add(documentSnapshot.toObject(Workmate.class));
    //                }
    //            }
    //        }
    //    }).addOnFailureListener(new OnFailureListener() {
    //        @Override
    //        public void onFailure(@NonNull Exception e) {
    //            Log.d(TAG, "Impossible to get joining workmates list", e);
    //        }
    //    });
    //}
}
