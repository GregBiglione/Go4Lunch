package com.greg.go4lunch.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.greg.go4lunch.MainActivity;
import com.greg.go4lunch.R;
import com.greg.go4lunch.model.Restaurant;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;

public class DetailedRestaurant extends AppCompatActivity {

    @BindView(R.id.detailed_restaurant_picture) ImageView mDetailedPicture;
    @BindView(R.id.detailed_restaurant_name) TextView mDetailedName;
    @BindView(R.id.detailed_restaurant_rating) RatingBar mDetailedRating;
    @BindView(R.id.detailed_restaurant_address) TextView mDetailedAddress;

    //@BindView(R.id.call_Layout) LinearLayout mCallLyt;
    //public static final int CALL_REQUEST_CODE = 218;

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
        //clickOnCall();
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
        String restaurantPhone = restaurant.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("Phone: " + restaurantPhone));
    }
//
    //// ---------------------------- Call function --------------------------------------------------------------------------------------------------
    //private void clickOnCall(){
    //    mCallLyt.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            checkPermissionToCall();
    //            Toasty.success(getApplicationContext(), "Click on call", Toasty.LENGTH_SHORT).show();
    //        }
    //    });
    //}
//
    //// ---------------------------- Check call permission ------------------------------------------
    //@Override
    //public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    //}
//
    //@AfterPermissionGranted(CALL_REQUEST_CODE)
    //private void checkPermissionToCall() {
    //    String[] perms = {CALL_PHONE};
    //    if (EasyPermissions.hasPermissions(getApplicationContext(), perms)){
    //        Toasty.success(this, "Call permission granted", Toasty.LENGTH_SHORT).show();
    //        //Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
    //        //startActivity(callIntent);
    //        startCall();
    //    }
    //    else{
    //        EasyPermissions.requestPermissions(this, "Permission to call required", CALL_REQUEST_CODE, perms);
    //    }
    //}
//
    //// ---------------------------- Start a call ---------------------------------------------------
    //private void startCall(){
    //    String phoneNumber = "+33618031946";
    //    Intent callIntent = new Intent(Intent.ACTION_DIAL);
    //    callIntent.setData(Uri.parse(phoneNumber));
    //    startActivity(callIntent);
    //}

    //private void startCall(){
    //    String perm = ;
    //    if (EasyPermissions.hasPermissions(getContext(), perms)){
    //        Toasty.success(getContext(), getString(R.string.location_granted), Toasty.LENGTH_SHORT).show();
//
    //    }
    //    else {
    //        EasyPermissions.requestPermissions(this,"We need your permission to locate you",
    //                LOCATION_PERMISSION_REQUEST_CODE, perm);
    //    }
    //}
    //public void clickOnCall(){
    //    mCallLyt.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            startCall();
    //        }
    //    });
    //}
//
    //private void startCall() {
    //    String phoneNumber = "+33618031946";
    //    if (ContextCompat.checkSelfPermission(DetailedRestaurant.this,
    //            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
    //        ActivityCompat.requestPermissions(DetailedRestaurant.this,
    //                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
    //    }
    //    else{
    //        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
    //        startActivity(intentCall);
    //    }
//
    //}
//
    //@Override
    //public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //    if (requestCode == REQUEST_CALL){
    //        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
    //            startCall();
    //        }
    //        else{
    //            Toasty.error(getApplicationContext(), "Click on call no good on RequestPermissions", Toasty.LENGTH_SHORT).show();
    //        }
    //    }
    //}
}
