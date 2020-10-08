package com.greg.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.greg.go4lunch.model.LikedRestaurant;
import com.greg.go4lunch.model.Workmate;

public class WorkmateHelper {

    private static final String WORKMATE_COLLECTION = "workmates";
    private static final String LIKED_RESTAURANTS_COLLECTION = "likedRestaurants";

    //-----------------------------------
    //--- WORKMATES ---------------------
    //-----------------------------------

    // ---------------------------- Collection reference -------------------------------------------
    public static CollectionReference getWorkmatesCollection(){
        return FirebaseFirestore.getInstance().collection(WORKMATE_COLLECTION);
    }

    // ---------------------------- Create workmate ------------------------------------------------
    public static Task<Void> createWorkmate(String uid, String picture, String name, String email, String pickedRestaurant, boolean joining){
        Workmate workmateToCreate = new Workmate(uid, picture, name, email, pickedRestaurant, joining);
        return WorkmateHelper.getWorkmatesCollection().document(uid).set(workmateToCreate);
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
    public static Task<Void> upDateIsJoining(String uid, boolean joining){
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("joining", joining);
    }

    //-------------------------------------------
    //--- LIKED RESTAURANTS ---------------------
    //-------------------------------------------

    public static CollectionReference getLikedRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(LIKED_RESTAURANTS_COLLECTION);
    }

    // ---------------------------- Create liked restaurant ----------------------------------------
    public static Task<Void> CreateLikedRestaurant(String uid, String idRestaurant, boolean favorite){
        LikedRestaurant likedRestaurant = new LikedRestaurant(uid, idRestaurant, favorite);
        return WorkmateHelper.getLikedRestaurantsCollection().document(uid).set(likedRestaurant);
    }

    public static Task<Void> upDateFavoriteRestaurant(String uid, String idRestaurant, boolean favorite){
        return WorkmateHelper.getLikedRestaurantsCollection().document(uid).update("idRestaurant", idRestaurant,"isFavorite", favorite);
    }
}
