package com.greg.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recoverIntent();
        clickOnCall();
        clickOnWebsite();
    }

    public void recoverIntent(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));

        //Bitmap picture = restaurant.getRestaurantPicture();
        //mDetailedPicture.setImageBitmap(picture);
        //String picture = restaurant.getRestaurantPicture();
        //Glide.with(this)
        //        .load(picture)
        //        .into(mDetailedPicture);

        String restaurantName = restaurant.getName();
        mDetailedName.setText(restaurantName);
        float restaurantRating = restaurant.getRating();
        mDetailedRating.setRating(restaurantRating);
        String restaurantAddress = restaurant.getAddress();
        mDetailedAddress.setText(restaurantAddress);
        //String restaurantPhone = restaurant.getPhoneNumber();
        //Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("Phone: " + restaurantPhone));
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
