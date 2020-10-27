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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
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
import com.greg.go4lunch.viewmodel.SharedViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CALL_PHONE;

public class DetailedRestaurant extends AppCompatActivity {

    @BindView(R.id.detailed_restaurant_picture) ImageView mDetailedPicture;
    @BindView(R.id.detailed_restaurant_name) TextView mDetailedName;
    @BindView(R.id.detailed_restaurant_rating) RatingBar mDetailedRating;
    @BindView(R.id.detailed_restaurant_address) TextView mDetailedAddress;

    @BindView(R.id.fab) FloatingActionButton mPickButton;

    public static final int CALL_REQUEST_CODE = 218;

    @BindView(R.id.like_Layout) LinearLayout mLikeLyt;
    @BindView(R.id.like_image) ImageView mLikeStar;
    @BindView(R.id.detailed_like) TextView mLikeText;
    boolean isFavorite;
    boolean isJoining;

    @BindView(R.id.joining_workmates_recycler) RecyclerView mJoiningWorkmatesRecyclerView;
    private JoiningWorkmatesAdapter mJoiningWorkmatesAdapter;
    private SharedViewModel mSharedViewModel;

    private Restaurant mRestaurant;

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

        configureJoiningWorkmatesRecyclerView();
        getFavorite();
        getSelectedRestaurant();
    }

    public void recoverIntent(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
        mRestaurant = restaurant;

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

    @OnClick(R.id.fab)
    void clickOnPick(){
        if (!isJoining){
            mPickButton.setImageResource(R.drawable.ic_check_circle_green_24dp);
            updateWorkmateIsJoining();
            isJoining = true;
        }
        else{
            mPickButton.setImageResource(R.drawable.ic_check_circle_white_24dp);
            updateWorkmateIsNotJoining();
            isJoining = false;
        }
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

    private void updateWorkmateIsJoining(){
        createWorkmateInFireStore();
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                String idPickedRestaurant = mRestaurant.getIdRestaurant();
                String namePickedRestaurant = mRestaurant.getName();
                if (currentWorkmate != null){
                    WorkmateHelper.updatePickedRestaurantAndIsJoining(currentWorkmate.getUid(), idPickedRestaurant, namePickedRestaurant, true);
                }
            }
        });
    }

    private void updateWorkmateIsNotJoining(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            WorkmateHelper.updatePickedRestaurantAndIsJoining(user.getUid(),null, null, false);
        }
    }

    private void getSelectedRestaurant(){
        mSharedViewModel.getPickedRestaurantData().observe(this, new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(ArrayList<Workmate> workmates) {
                if(!workmates.isEmpty()){
                    mPickButton.setImageResource(R.drawable.ic_check_circle_green_24dp);
                    isJoining = true;
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
        String phoneNumber = "tel:" + mRestaurant.getPhoneNumber();
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

    @OnClick(R.id.like_Layout)
    void clickOnLike(){
        if (!isFavorite){
            mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            mLikeText.setText(R.string.likedDetailedText);
            mLikeText.setTextColor(getResources().getColor(R.color.colorStar));
            addFavorite();
            isFavorite = true;
        }
        else{
            mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
            mLikeText.setText(R.string.detailed_like);
            mLikeText.setTextColor(getResources().getColor(R.color.colorPrimary));
            upDateFavorite();
            isFavorite = false;
        }
    }

    private void addFavorite(){
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                if (currentWorkmate != null){
                    String uid = currentWorkmate.getUid();
                    String idRestaurant = mRestaurant.getIdRestaurant();

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

    private void getFavorite(){
        mSharedViewModel.getFavoriteRestaurantData().observe(this, new Observer<ArrayList<LikedRestaurant>>() {
            @Override
            public void onChanged(ArrayList<LikedRestaurant> likedRestaurants) {
                if (!likedRestaurants.isEmpty()){
                    mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
                    mLikeText.setText(R.string.likedDetailedText);
                    mLikeText.setTextColor(getResources().getColor(R.color.colorStar));
                    isFavorite = true;
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Go to website function -----------------------------------------
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.website_Layout)
    void clickOnWebsite(){ goToWebsite(); }

    private void goToWebsite(){
        String website = mRestaurant.getWebsite();
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        startActivity(websiteIntent);
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
        mSharedViewModel.initPickedRestaurant(this, getCurrentUser().getUid(), restaurantId);
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
                //Intent i = getIntent();
                //Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
                //if (!workmates.isEmpty()){
                //    restaurant.setJoiningNumber(workmates.size());
                //}

                mJoiningWorkmatesAdapter = new JoiningWorkmatesAdapter(workmates);
                mJoiningWorkmatesRecyclerView.setAdapter(mJoiningWorkmatesAdapter);

                //mJoiningWorkmatesAdapter.notifyDataSetChanged();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get restaurant photo -------------------------------------------
    //----------------------------------------------------------------------------------------------

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
