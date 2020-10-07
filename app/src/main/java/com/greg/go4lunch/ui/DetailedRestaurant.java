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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.greg.go4lunch.RestaurantAdapter;
import com.greg.go4lunch.api.WorkmateHelper;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    boolean isJoiningRestaurant;
    private Workmate mWorkmate;

    @BindView(R.id.call_Layout) LinearLayout mCallLyt;
    public static final int CALL_REQUEST_CODE = 218;

    @BindView(R.id.like_Layout) LinearLayout mLikeLyt;
    @BindView(R.id.like_image) ImageView mLikeStar;
    @BindView(R.id.detailed_like) TextView mLikeText;
    boolean isFavorite;

    @BindView(R.id.website_Layout) LinearLayout mWebsiteLyt;

    @BindView(R.id.joining_workmates_recycler) RecyclerView mJoiningWorkmatesRecyclerView;
    private JoiningWorkmatesAdapter mJoiningWorkmatesAdapter;
    private SharedViewModel mSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mSharedViewModel = new ViewModelProvider(DetailedRestaurant.this).get(SharedViewModel.class);
        //mSharedViewModel.initJoiningWorkmates(this);
        setContentView(R.layout.activity_detailed_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        recoverIntent();

        defaultPickIcon();
        clickOnJoin();

        clickOnCall();
        clickOnLike();
        clickOnWebsite();

        //configureJoiningWorkmatesRecyclerView();
    }

    public void recoverIntent(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));

        String restaurantName = restaurant.getName();
        mDetailedName.setText(restaurantName);
        float restaurantRating = restaurant.getRating();
        mDetailedRating.setRating(restaurantRating);
        String restaurantAddress = restaurant.getAddress();
        mDetailedAddress.setText(restaurantAddress);
        getRestaurantPhoto(mDetailedPicture, restaurant.getRestaurantPicture());
    }

    // ---------------------------- Click on join button --------------------------------------------------------------------------------------------------
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
                    updateIsJoining();
                    restaurantIsPicked();
                    //TODO change restaurant marker color to green for this restaurant
                }
                else{
                    mPickButton.setImageResource(R.drawable.ic_check_circle_white_24dp);
                    isJoiningRestaurant = false;
                    updateIsNotJoining();
                    restaurantNotPicked();
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

    // ---------------------------- Call function --------------------------------------------------------------------------------------------------
    public void clickOnCall(){
        mCallLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall();
            }
        });
    }

    // ---------------------------- Check call permission ------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // ---------------------------- Start a call ---------------------------------------------------
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

    // ---------------------------- Add favorite function -----------------------------------------------------------------------------------------------
    public void clickOnLike(){
        mLikeLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite){
                    mLikeStar.setImageResource(R.drawable.ic_star_yellow_24dp);
                    mLikeText.setText(R.string.likedDetailedText);
                    mLikeText.setTextColor(getResources().getColor(R.color.colorStar));
                    addFavorite();
                    //isFavorite = true;

                }
                else {
                    mLikeStar.setImageResource(R.drawable.ic_star_orange_24dp);
                    mLikeText.setText(R.string.detailed_like);
                    mLikeText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    upDateFavorite();
                    //isFavorite = false;
                }
            }
        });
    }

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

                    // ------------ Create liked restaurant in Firestore ------------------
                    WorkmateHelper.CreateLikedRestaurant(uid, idRestaurant, true);
                    Toasty.success(getApplicationContext(), "Favorite restaurant created in Firestore after click on star button",
                            Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upDateFavorite(){
        WorkmateHelper.upDateFavoriteRestaurant(null, null, false);
        Toasty.warning(getApplicationContext(), "Favorite restaurant removed from Firestore after click on star button",
                Toasty.LENGTH_SHORT).show();
    }

    // ---------------------------- Go to website function -----------------------------------------------------------------------------------------------
    private void clickOnWebsite(){
        mWebsiteLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWebsite();
            }
        });
    }

    private void goToWebsite(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
        String website = restaurant.getWebsite();
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        startActivity(websiteIntent);
    }

    // ---------------------------- Get current user -------------------------------------------------------------------------------------------------
    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    // ---------------------------- Update workmate is joining ----------------------------------------------------------------------------------------------
    private void updateIsJoining() {
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if(user != null){
        //    WorkmateHelper.upDateIsJoining(user.getUid(), true);
        //}
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                if (currentWorkmate != null){
                    WorkmateHelper.upDateIsJoining(currentWorkmate.getUid(), true);
                }
            }
        });
    }

    private void updateIsNotJoining() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            WorkmateHelper.upDateIsJoining(user.getUid(), false);
        }
    }

    // ---------------------------- Update picked restaurant ----------------------------------------------------------------------------------------------
    private void restaurantIsPicked(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));
        String namePickedRestaurant = restaurant.getName();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            WorkmateHelper.upDatePickedRestaurant(user.getUid(), namePickedRestaurant);
        }
    }

    private void restaurantNotPicked(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            WorkmateHelper.upDatePickedRestaurant(user.getUid(), null);
        }
    }

    // ---------------------------- Configure recyclerview ----------------------------------------------------------------------------------------------
    //private void configureJoiningWorkmatesRecyclerView() {
    //    mJoiningWorkmatesRecyclerView = findViewById(R.id.joining_workmates_recycler);
    //    mJoiningWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    //    mJoiningWorkmatesAdapter = new JoiningWorkmatesAdapter(mSharedViewModel.getJoiningWorkmatesData().getValue());
    //    mJoiningWorkmatesAdapter.notifyDataSetChanged();
    //}

    // ---------------------------- Back to restaurants list ----------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
