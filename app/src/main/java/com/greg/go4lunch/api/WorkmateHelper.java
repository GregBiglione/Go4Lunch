package com.greg.go4lunch.api;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.greg.go4lunch.model.LikedRestaurant;
import com.greg.go4lunch.model.Workmate;

public class WorkmateHelper {

    private static final String WORKMATE_COLLECTION = "workmates";
    private static final String LIKED_RESTAURANTS_COLLECTION = "likedRestaurants";

    //----------------------------------------------------------------------------------------------
    //----------------------------- Workmates ------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    // ---------------------------- Collection reference -------------------------------------------
    public static CollectionReference getWorkmatesCollection(){
        return FirebaseFirestore.getInstance().collection(WORKMATE_COLLECTION);
    }

    //----------------------------- Create workmate ------------------------------------------------
    public static Task<Void> createWorkmate(String uid, String picture, String name, String email, String idPickedRestaurant,
                                            String pickedRestaurant, String addressRestaurant, /*PhotoMetadata photoRestaurant,*/
                                            float ratingRestaurant, String websiteRestaurant,
                                            String phoneRestaurant, String distanceFromUser, /*LatLng latLng,*/ int joiningNumber,
            int openingHour, boolean joining){
        Workmate workmateToCreate = new Workmate(uid, picture, name, email, pickedRestaurant, idPickedRestaurant,
                addressRestaurant, /*photoRestaurant,*/ ratingRestaurant, websiteRestaurant, phoneRestaurant, distanceFromUser, /*latLng,*/ joiningNumber, openingHour,
                joining);
        return WorkmateHelper.getWorkmatesCollection().document(uid).set(workmateToCreate);
    }

    //----------------------------- Get current workmate -------------------------------------------
    public static Task<DocumentSnapshot> getWorkmate(String uid){
        return WorkmateHelper.getWorkmatesCollection().document(uid).get();
    }

    public static Task<Void> updatePickedRestaurantAndIsJoining(String uid,  String idPickedRestaurant, String pickedRestaurant,
                                                                String addressRestaurant, /*PhotoMetadata photoRestaurant,*/
                                                                float ratingRestaurant,
                                                                String websiteRestaurant, String phoneRestaurant, String distanceFromUser, /*LatLng latLng,*/
            int joiningNumber, int openingHour, boolean joining){
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("idPickedRestaurant", idPickedRestaurant,
                "pickedRestaurant", pickedRestaurant,"addressRestaurant", addressRestaurant, /*"photoRestaurant", photoRestaurant,*/
                "ratingRestaurant", ratingRestaurant, "websiteRestaurant", websiteRestaurant, "phoneRestaurant", phoneRestaurant, "distanceFromUser",
                distanceFromUser, /*"latLng", latLng,*/ "joiningNumber", joiningNumber, "openingHour", openingHour,"joining", joining);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Liked restaurant -----------------------------------------------
    //----------------------------------------------------------------------------------------------

    public static CollectionReference getLikedRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(LIKED_RESTAURANTS_COLLECTION);
    }

    //----------------------------- Create liked restaurant ----------------------------------------
    public static Task<Void> createLikedRestaurant(String uid, String idPickedRestaurant, boolean favorite){
        LikedRestaurant likedRestaurant = new LikedRestaurant(uid, idPickedRestaurant, favorite);
        return WorkmateHelper.getLikedRestaurantsCollection().document(uid).set(likedRestaurant);
    }

    //----------------------------- Get liked restaurant -------------------------------------------
    public static Task<DocumentSnapshot> getLikedRestaurant(String uid){
        return WorkmateHelper.getLikedRestaurantsCollection().document(uid).get();
    }

    //----------------------------- update favorite ------------------------------------------------
    public static Task<Void> upDateFavoriteRestaurant(String uid, String idPickedRestaurant, boolean favorite){
        return WorkmateHelper.getLikedRestaurantsCollection().document(uid).update("restaurantId", idPickedRestaurant,"isFavorite", favorite);
    }
}
