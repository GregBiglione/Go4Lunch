package com.greg.go4lunch.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;

import com.greg.go4lunch.R;
import com.greg.go4lunch.model.Restaurant;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedRestaurant extends AppCompatActivity {

    @BindView(R.id.detailed_restaurant_picture) ImageView mDetailedPicture;

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
    }

    private void recoverIntent(){
        Intent i = getIntent();
        Restaurant restaurant = Parcels.unwrap(i.getParcelableExtra("RestaurantDetails"));

        Bitmap picture = restaurant.getRestaurantPicture();
        mDetailedPicture.setImageBitmap(picture);


    }
}
