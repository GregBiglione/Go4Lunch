package com.greg.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.greg.go4lunch.model.Workmate;

public class WorkmateHelper {

    private static final String COLLECTION_NAME = "workmates";

    // ---------------------------- Collection reference -------------------------------------------
    public static CollectionReference getWorkmatesCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // ---------------------------- Create workmate ------------------------------------------------
    public static Task<Void> createWorkmate(String uid, String picture, String name, String email, String pickedRestaurant, boolean isJoining){
        Workmate workmateToCreate = new Workmate(uid, picture, name, email, pickedRestaurant, isJoining);
        return WorkmateHelper.getWorkmatesCollection().document(uid).set(workmateToCreate);
    }

    // ---------------------------- Get all workmates ----------------------------------------------
    public static Task<DocumentSnapshot> getAllWorkmates(){
        return WorkmateHelper.getWorkmatesCollection().document().get();
    }

    // ---------------------------- Get current workmate -------------------------------------------
    public static Task<DocumentSnapshot> getWorkmate(String uid){
        return WorkmateHelper.getWorkmatesCollection().document(uid).get();
    }

    // ---------------------------- Update PickedRestaurant ----------------------------------------
    public static Task<Void> upDatePickedRestaurant(String uid, String pickedRestaurant){
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("pickedRestaurant", pickedRestaurant);
    }

    // ---------------------------- Update isJoining -----------------------------------------------
    public static Task<Void> upDateIsJoining(String uid, boolean isJoining){
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("isJoining", isJoining);
    }
}
