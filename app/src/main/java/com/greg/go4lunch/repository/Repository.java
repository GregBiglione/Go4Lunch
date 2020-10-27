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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Repository";

    public static Repository getInstance(Context context){
        if (instance == null){
            instance = new Repository();
        }
        return instance;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get all workmates ----------------------------------------------
    //----------------------------------------------------------------------------------------------

    public MutableLiveData<ArrayList<Workmate>> getAllWorkmates(){
        MutableLiveData<ArrayList<Workmate>> allWorkmates = new MutableLiveData<>();
        db.collection("workmates")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    ArrayList<Workmate> workmates = new ArrayList<>();
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

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get joining workmates ------------------------------------------
    //----------------------------------------------------------------------------------------------

    public MutableLiveData<ArrayList<Workmate>> getJoiningWorkmates(String idPickedRestaurant){
        MutableLiveData<ArrayList<Workmate>> allJoiningWorkmates = new MutableLiveData<>();
        db.collection("workmates")
                .whereEqualTo("idPickedRestaurant", idPickedRestaurant)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> joiningList = queryDocumentSnapshots.getDocuments();
                    ArrayList<Workmate> joiningWorkmates = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : joiningList) {
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

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get favorite restaurant ----------------------------------------
    //----------------------------------------------------------------------------------------------

    public MutableLiveData<ArrayList<LikedRestaurant>> getFavoriteRestaurant(String uid, String idLikedRestaurant){
        MutableLiveData<ArrayList<LikedRestaurant>> allFavorites = new MutableLiveData<>();
        db.collection("likedRestaurants")
                .whereEqualTo("workmateId", uid)
                .whereEqualTo("restaurantId", idLikedRestaurant)
                .whereEqualTo("isFavorite", true)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> favoriteList = queryDocumentSnapshots.getDocuments();
                    ArrayList<LikedRestaurant> favorites = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : favoriteList) {
                        favorites.add(documentSnapshot.toObject(LikedRestaurant.class));
                    }
                    allFavorites.setValue(favorites);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Impossible to get favorite list", e);
            }
        });
        return allFavorites;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get picked restaurant ------------------------------------------
    //----------------------------------------------------------------------------------------------

    public MutableLiveData<ArrayList<Workmate>> getPickedRestaurant(String uid, String idPickedRestaurant){
        MutableLiveData<ArrayList<Workmate>> pickedRestaurant = new MutableLiveData<>();
        db.collection("workmates")
                .whereEqualTo("uid", uid)
                .whereEqualTo("idPickedRestaurant", idPickedRestaurant)
                .whereEqualTo("joining", true)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> pickedList = queryDocumentSnapshots.getDocuments();
                    ArrayList<Workmate> picked = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot: pickedList) {
                        picked.add(documentSnapshot.toObject(Workmate.class));
                    }
                    pickedRestaurant.setValue(picked);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Impossible to get picked list", e);
            }
        });
        return pickedRestaurant;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get selected restaurant for map marker -------------------------
    //----------------------------------------------------------------------------------------------

    public MutableLiveData<ArrayList<Workmate>> getSelected(String idPickedRestaurant){
        MutableLiveData<ArrayList<Workmate>> selectedRestaurant = new MutableLiveData<>();
        db.collection("workmates")
                .whereEqualTo("idPickedRestaurant", idPickedRestaurant)
                .whereEqualTo("joining", true)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> selectedList = queryDocumentSnapshots.getDocuments();
                    ArrayList<Workmate> selected = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : selectedList) {
                        selected.add(documentSnapshot.toObject(Workmate.class));
                    }
                    selectedRestaurant.setValue(selected);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Impossible to get selected list", e);
            }
        });
        return selectedRestaurant;
    }
}
