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

    public static Repository getInstance(Context context){
        if (instance == null){
            instance = new Repository();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<Workmate>> getAllWorkmates(){
        loadWorkmates();
        MutableLiveData<ArrayList<Workmate>> allWorkmates = new MutableLiveData<>();
        allWorkmates.setValue(workmates);
        return allWorkmates;
    }

    private void loadWorkmates() {
        db.collection("workmates").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
}
