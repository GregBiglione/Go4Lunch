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
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.greg.go4lunch.R;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;

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

    @BindView(R.id.website_Layout) LinearLayout mWebsiteLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        recoverIntent();

        defaultPickIcon();
        clickOnJoin();

        clickOnCall();
        clickOnWebsite();
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
        Workmate workmate = new Workmate();
        String restaurantName = workmate.getPickedRestaurant();
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantName == null){
                    mPickButton.setImageResource(R.drawable.ic_check_circle_green_24dp);
                    isJoiningRestaurant = true;
                    //TODO change restaurant marker color to green for this restaurant
                }
                else{
                    mPickButton.setImageResource(R.drawable.ic_check_circle_white_24dp);
                    isJoiningRestaurant = false;
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
}
