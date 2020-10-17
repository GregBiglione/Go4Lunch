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
import com.greg.go4lunch.model.LikedRestaurant;
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

    //----------------------------- Get all workmates ----------------------------------------------
    public MutableLiveData<ArrayList<Workmate>> getAllWorkmates(){
        MutableLiveData<ArrayList<Workmate>> allWorkmates = new MutableLiveData<>();
        db.collection("workmates")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : list) {
                        workmates.add(documentSnapshot.toObject(Workmate.class));
                    }
                    allWorkmates.setValue(workmates);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Impossible to get workmates list", e);
            }
        });
        return allWorkmates;
    }

    //----------------------------- Get joining workmates ------------------------------------------
    public MutableLiveData<ArrayList<Workmate>> getJoiningWorkmates(String uid, String idPickedRestaurant){
        MutableLiveData<ArrayList<Workmate>> allJoiningWorkmates = new MutableLiveData<>();
        db.collection("workmates")
                .whereEqualTo("uid", uid)
                .whereEqualTo("idPickedRestaurant", idPickedRestaurant)
                .whereEqualTo("joining", true)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> joiningList = queryDocumentSnapshots.getDocuments();
                    ArrayList<Workmate> joiningWorkmates = new ArrayList<>();
                    for ( DocumentSnapshot documentSnapshot : joiningList) {
                        joiningWorkmates.add(documentSnapshot.toObject(Workmate.class));
                    }
                    allJoiningWorkmates.setValue(joiningWorkmates);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Impossible to get joining workmates list", e);
            }
        });
        return allJoiningWorkmates;
    }

    //----------------------------- Get favorite restaurant ----------------------------------------
    //4)
    //public MutableLiveData<ArrayList<LikedRestaurant>> getFavoriteRestaurant(String uid, String idPickedRestaurant){
    //   MutableLiveData<ArrayList<LikedRestaurant>> allFavorites = new MutableLiveData<>();
    //   db.collection("likedRestaurants")
    //           .whereEqualTo("workmateId", uid)
    //           .whereEqualTo("restaurantId", idPickedRestaurant)
    //           .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
    //       @Override
    //       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
    //           if (!queryDocumentSnapshots.isEmpty()){
    //               List<DocumentSnapshot> favoriteList = queryDocumentSnapshots.getDocuments();
    //               //5)
    //               ArrayList<LikedRestaurant> favorites = new ArrayList<>();
    //               for (DocumentSnapshot documentSnapshot : favoriteList) {
    //                   favorites.add(documentSnapshot.toObject(LikedRestaurant.class));
    //               }
    //               allFavorites.setValue(favorites);
    //           }
    //       }
    //   }).addOnFailureListener(new OnFailureListener() {
    //       @Override
    //       public void onFailure(@NonNull Exception e) {
    //           Log.d(TAG, "Impossible to get favorite list", e);
    //       }
    //   });
    //   return allFavorites;
    //}
}
