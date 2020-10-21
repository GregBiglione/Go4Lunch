package com.greg.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.greg.go4lunch.JoiningWorkmatesAdapter;
import com.greg.go4lunch.R;
import com.greg.go4lunch.api.WorkmateHelper;
import com.greg.go4lunch.model.LikedRestaurant;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.ui.home.HomeFragment;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import org.parceler.Parcels;
import org.parceler.Repository;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CALL_PHONE;
import static androidx.test.InstrumentationRegistry.getContext;

public class DetailedRestaurant extends AppCompatActivity {

    @BindView(R.id.detailed_restaurant_picture) ImageView mDetailedPicture;
    @BindView(R.id.detailed_restaurant_name) TextView mDetailedName;
    @BindView(R.id.detailed_restaurant_rating) RatingBar mDetailedRating;
    @BindView(R.id.detailed_restaurant_address) TextView mDetailedAddress;

    @BindView(R.id.fab) FloatingActionButton mPickButton;
    boolean isJoiningRestaurant;

    public static final int CALL_REQUEST_CODE = 218;

    @BindView(R.id.like_Layout) LinearLayout mLikeLyt;
    @BindView(R.id.like_image) ImageView mLikeStar;
    @BindView(R.id.detailed_like) TextView mLikeText;
    //boolean isFavorite;

    @BindView(R.id.joining_workmates_recycler) RecyclerView mJoiningWorkmatesRecyclerView;
    private JoiningWorkmatesAdapter mJoiningWorkmatesAdapter;
    private SharedViewModel mSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
        setContentView(R.layout.activity_detailed_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        recoverIntent();

        defaultPickIcon();
        clickOnJoin();

        //defaultLikeIcon();

        configureJoiningWorkmatesRecyclerView();
        //isFavorite();
        //getFavoriteRestaurant();
        //getFavorite();
    }

    public void recoverIntent(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));

        //String restaurantId = restaurant.getIdRestaurant();
        String restaurantName = restaurant.getName();
        mDetailedName.setText(restaurantName);
        float restaurantRating = restaurant.getRating();
        mDetailedRating.setRating(restaurantRating);
        String restaurantAddress = restaurant.getAddress();
        mDetailedAddress.setText(restaurantAddress);
        getRestaurantPhoto(mDetailedPicture, restaurant.getRestaurantPicture());
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Click on join button -------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void defaultPickIcon(){
        if (!isJoiningRestaurant){
            mPickButton.setImageResource(R.drawable.ic_check_circle_white_24dp);
        }
        else{
            mPickButton.setImageResource(R.drawable.ic_check_circle_green_24dp);
        }
    }

    public void clickOnJoin() {
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isJoiningRestaurant){
                    mPickButton.setImageResource(R.drawable.ic_check_circle_green_24dp);
                    isJoiningRestaurant = true;
                    //updateIsJoining();
                    //restaurantIsPicked();
                    updateRestaurantAndWorkmateJoiningData();
                    //TODO change restaurant marker color to green for this restaurant
                }
                else{
                    mPickButton.setImageResource(R.drawable.ic_check_circle_white_24dp);
                    isJoiningRestaurant = false;
                    //updateIsNotJoining();
                    //restaurantNotPicked();
                    updateRestaurantAndWorkmateIsNotJoiningData();
                    //TODO change restaurant marker color to orange for this restaurant
                }
            }
        });
    }

    private void getRestaurantPhoto(ImageView v, PhotoMetadata photoMetadata){

        // ---------------------------- Create a FetchPhotoRequest -------------------------
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .build();
        PlacesClient mPlacesClient = Places.createClient(this);
        mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
            @Override
            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                Glide.with(DetailedRestaurant.this)
                        .load(bitmap)
                        .into(v);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    final ApiException apiException = (ApiException) e;
                    final int statusCode = apiException.getStatusCode();
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Call function --------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.call_Layout)
    void clickOnCall(){ startCall(); }

    //----------------------------------------------------------------------------------------------
    //---------------------------- Check call permission -------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Start a call ---------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(CALL_REQUEST_CODE)
    private void startCall(){
        String perm = CALL_PHONE;
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
        String phoneNumber = "tel:" + restaurant.getPhoneNumber();
        if (EasyPermissions.hasPermissions(this, perm)){
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
            startActivity(callIntent);
        }
        else {
            EasyPermissions.requestPermissions(this,"We need your permission to locate you",
                    CALL_REQUEST_CODE, perm);
            Toasty.warning(this, "Request permission", Toasty.LENGTH_SHORT).show();
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Add favorite function ------------------------------------------
    //----------------------------------------------------------------------------------------------

    //private void defaultLikeIcon(){
    //    if (isFavorite){
    //        mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
    //        mLikeText.setText(R.string.detailed_like);
    //        mLikeText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    //    }
    //    else{
    //        mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
    //        mLikeText.setText(R.string.likedDetailedText);
    //        mLikeText.setTextColor(ContextCompat.getColor(this, R.color.colorStar));
    //    }
    //}

    //@OnClick(R.id.like_Layout)
    //void clickOnLike(){
    //    if (!isFavorite){
    //        mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
    //        mLikeText.setText(R.string.likedDetailedText);
    //        mLikeText.setTextColor(getResources().getColor(R.color.colorStar));
    //        addFavorite();
    //        isFavorite = true;
    //    }
    //    else{
    //        mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
    //        mLikeText.setText(R.string.detailed_like);
    //        mLikeText.setTextColor(getResources().getColor(R.color.colorPrimary));
    //        upDateFavorite();
    //        isFavorite = false;
    //    }
    //}

    //@OnClick(R.id.like_Layout)
    //void clickOnLike(){
    //    Intent i = getIntent();
    //    Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
    //    //boolean isFavorite = false;
    //    String restaurantId = restaurant.getIdRestaurant();
    //    mSharedViewModel.initFavoriteRestaurant(this, getCurrentUser().getUid(), restaurantId);
    //    //mSharedViewModel.getFavoriteRestaurantData().getValue().get(0).getIsFavorite();
//
    //    //if(mSharedViewModel.getFavoriteRestaurantData().getValue().get(0).getIsFavorite()){
    //    //    isFavorite = true;
    //    //}
    //    //if(mSharedViewModel.getFavoriteRestaurantData() != null){
    //    //    isFavorite = true;
    //    //}
//
    //    //if (isFavorite){
    //    //    mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
    //    //    mLikeText.setText(R.string.detailed_like);
    //    //    mLikeText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    //    //}
    //    //else{
    //    //    mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
    //    //    mLikeText.setText(R.string.likedDetailedText);
    //    //    mLikeText.setTextColor(ContextCompat.getColor(this, R.color.colorStar));
    //    //    //isFavoriteTemporary = true;
    //    //}
//
    //    if (!isFavorite){
    //        mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
    //        mLikeText.setText(R.string.likedDetailedText);
    //        mLikeText.setTextColor(getResources().getColor(R.color.colorStar));
    //        addFavorite();
    //        isFavorite = true;
    //    }
    //    else{
    //        mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
    //        mLikeText.setText(R.string.detailed_like);
    //        mLikeText.setTextColor(getResources().getColor(R.color.colorPrimary));
    //        upDateFavorite();
    //        //deleteFavorite();
    //        isFavorite = false;
    //    }
    //}

    private void addFavorite(){
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                if (currentWorkmate != null){
                    String uid = currentWorkmate.getUid();

                    Intent i = getIntent();
                    Restaurant restaurantLiked = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));

                    String idRestaurant = restaurantLiked.getIdRestaurant();

                    //------------- Create liked restaurant in FireStore ---------------------------
                    WorkmateHelper.createLikedRestaurant(uid, idRestaurant, true);
                    Toasty.success(getApplicationContext(), "Favorite restaurant created in Firestore after click on star button",
                            Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upDateFavorite(){
        WorkmateHelper.getLikedRestaurant(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                LikedRestaurant currentLikedRestaurant = documentSnapshot.toObject(LikedRestaurant.class);
                if(currentLikedRestaurant != null){
                    WorkmateHelper.upDateFavoriteRestaurant(currentLikedRestaurant.getWorkmateId(), null, false);
                    Toasty.warning(getApplicationContext(), "Favorite restaurant removed from Firestore after click on star button",
                            Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    //private void getFavorite(){
    //    WorkmateHelper.getLikedRestaurant(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    //        @Override
    //        public void onSuccess(DocumentSnapshot documentSnapshot) {
    //            LikedRestaurant currentLikedRestaurant = documentSnapshot.toObject(LikedRestaurant.class);
    //            if (currentLikedRestaurant != null){
    //                mSharedViewModel.getFavoriteRestaurantData().
    //            }
    //        }
    //    });
    //}

   //private void getFavoriteRestaurant(){
   //    WorkmateHelper.getLikedRestaurant(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
   //        @Override
   //        public void onSuccess(DocumentSnapshot documentSnapshot) {
   //            LikedRestaurant currentLikedRestaurant = documentSnapshot.toObject(LikedRestaurant.class);
   //            if (currentLikedRestaurant != null){
   //                String idFavoriteRestaurant = currentLikedRestaurant.getRestaurantId();
   //                boolean isFavoriteRestaurant = currentLikedRestaurant.getIsFavorite(); //false by default
   //                if (!isFavoriteRestaurant && idFavoriteRestaurant != null){
   //                    isFavorite = true;
   //                }
   //            }
   //        }
   //    });
   //}

    //----------------------------- Delete ????:  No -------------------------------------------------------
    //private void deleteFavorite(){
    //    WorkmateHelper.getLikedRestaurant(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    //        @Override
    //        public void onSuccess(DocumentSnapshot documentSnapshot) {
    //            LikedRestaurant likedRestaurantToDelete = documentSnapshot.toObject(LikedRestaurant.class);
    //            if (likedRestaurantToDelete != null){
    //                WorkmateHelper.deleteFavoriteRestaurant(likedRestaurantToDelete.getWorkmateId());
    //                Toasty.error(getApplicationContext(), "Favorite restaurant delete from Firestore after click on star button",
    //                        Toasty.LENGTH_SHORT).show();
    //            }
    //        }
    //    });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Restaurant is favorite -----------------------------------------
    //----------------------------------------------------------------------------------------------
    //public void isFavorite(){
    //    Intent i = getIntent();
    //    Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
//
    //    boolean isFavoriteTemporary = false;
    //    String restaurantId = restaurant.getIdRestaurant();
    //    mSharedViewModel.initFavoriteRestaurant(this, getCurrentUser().getUid(), restaurantId);
    //    //mSharedViewModel.getFavoriteRestaurantData();
    //    if(mSharedViewModel.getFavoriteRestaurantData() != null){
    //        isFavoriteTemporary = true;
    //    }
//
    //    if (isFavoriteTemporary){
    //        mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
    //        mLikeText.setText(R.string.likedDetailedText);
    //        mLikeText.setTextColor(ContextCompat.getColor(this, R.color.colorStar));
    //        addFavorite();
    //    }
    //    else{
    //        mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
    //        mLikeText.setText(R.string.detailed_like);
    //        mLikeText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    //        upDateFavorite();
    //    }
//
    //}

    //private void getFavorite(){
    //    mSharedViewModel.getFavoriteRestaurantData().observe(this, new Observer<ArrayList<LikedRestaurant>>() {
    //        @Override
    //        public void onChanged(ArrayList<LikedRestaurant> likedRestaurants) {
    //            String uid = likedRestaurants.get(0).getWorkmateId();
    //            String idRestaurant = likedRestaurants.get(0).getRestaurantId();
    //            boolean isFavorite = likedRestaurants.get(0).getIsFavorite();
    //            if (uid != null && idRestaurant != null){
    //                for (LikedRestaurant l: likedRestaurants) {
    //                    mLikeLyt.setOnClickListener(new View.OnClickListener() {
    //                        @Override
    //                        public void onClick(View v) {
    //                            if (!isFavorite){
    //                                mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
    //                                mLikeText.setText(R.string.likedDetailedText);
    //                                mLikeText.setTextColor(getResources().getColor(R.color.colorStar));
    //                                addFavorite();
    //                                l.setFavorite(true);
    //                            }
    //                            else{
    //                                mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
    //                                mLikeText.setText(R.string.detailed_like);
    //                                mLikeText.setTextColor(getResources().getColor(R.color.colorPrimary));
    //                                upDateFavorite();
    //                                l.setFavorite(false);
    //                            }
    //                        }
    //                    });
    //                }
    //            }
//
    //        }
    //    });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Go to website function -----------------------------------------
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.website_Layout)
    void clickOnWebsite(){ goToWebsite(); }

    private void goToWebsite(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
        String website = restaurant.getWebsite();
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        startActivity(websiteIntent);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Create user in FireStore ---------------------------------------
    //----------------------------------------------------------------------------------------------

    private void createWorkmateInFireStore(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo = user.getPhotoUrl().toString();

            //----------------------------- Create workmate in FireStore ---------------------------
            WorkmateHelper.createWorkmate(uid, photo, name, email, null,  null, false);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get current user -----------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Update restaurant & is joining ---------------------------------
    //----------------------------------------------------------------------------------------------

    private void updateRestaurantAndWorkmateJoiningData(){
        createWorkmateInFireStore();
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                Intent i = getIntent();
                Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
                String idPickedRestaurant = restaurant.getIdRestaurant();
                String namePickedRestaurant = restaurant.getName();
                if (currentWorkmate != null){
                    WorkmateHelper.updatePickedRestaurantAndIsJoining(currentWorkmate.getUid(), idPickedRestaurant, namePickedRestaurant, true);
                }
            }
        });
    }

    private void updateRestaurantAndWorkmateIsNotJoiningData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            WorkmateHelper.updatePickedRestaurantAndIsJoining(user.getUid(),null, null, false);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure view model -------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void configureViewModel(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
        String restaurantId = restaurant.getIdRestaurant();

        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mSharedViewModel.initJoiningWorkmates(this, restaurantId);
        mSharedViewModel.initFavoriteRestaurant(this, getCurrentUser().getUid(), restaurantId);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure recycler view ----------------------------------------
    //----------------------------------------------------------------------------------------------

    private void configureJoiningWorkmatesRecyclerView() {
        mJoiningWorkmatesRecyclerView = findViewById(R.id.joining_workmates_recycler);
        //mJoiningWorkmatesRecyclerView.setHasFixedSize(true);
        mJoiningWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSharedViewModel.getJoiningWorkmatesData().observe(this, new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(ArrayList<Workmate> workmates) {
                mJoiningWorkmatesAdapter = new JoiningWorkmatesAdapter(workmates);
                mJoiningWorkmatesRecyclerView.setAdapter(mJoiningWorkmatesAdapter);
                //mJoiningWorkmatesAdapter.notifyDataSetChanged();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Back to restaurants list ---------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
