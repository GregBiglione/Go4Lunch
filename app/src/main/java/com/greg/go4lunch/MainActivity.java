package com.greg.go4lunch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public BottomNavigationView mBottomNavigationView;
    public static final String API_KEY = BuildConfig.ApiKey;
    PlacesClient mPlacesClient;

    public static final String TAG = "MainActivity";
    //@BindView(R.id.search) MenuItem mSearch;
    public Menu mSearchMenu;

    private FirebaseAuth mAuth;
    @BindView(R.id.user_name) TextView mName;
    @BindView(R.id.user_mail) TextView mMail;
    @BindView(R.id.user_photo) ImageView mPhoto;

    public NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationBottomMenu();
        initPlaces();
        autocompleteSupportFragInit();

        currentLoggedUserInformation();

        navigationViewMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    // ---------------------------- Search Menu ----------------------------------------------------
    //@Override
    //public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    //    View autocompleteBar = findViewById(R.id.autocomplete_linear_layout);
    //    if (item.getItemId() == R.id.search) {
    //        autocompleteBar.setVisibility(View.VISIBLE);
    //    } else {
    //        autocompleteBar.setVisibility(View.GONE);
    //    }
    //    return super.onOptionsItemSelected(item);
    //}

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // ---------------------------- Bottom Navigation Menu -----------------------------------------
    public void navigationBottomMenu(){
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_maps:
                    selectedFragment = new NavHostFragment();
                    break;
                case R.id.nav_list:
                    selectedFragment = new ListFragment();
                    break;
                case R.id.nav_workmates:
                    selectedFragment = new WorkmatesFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };

    // ---------------------------- Get places -----------------------------------------------------
    public void initPlaces(){
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), API_KEY);
        }
        mPlacesClient = Places.createClient(this);
    }

    // ---------------------------- Autocomplete support fragment initialization -------------------
    public void autocompleteSupportFragInit(){
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                //Log.i(TAG, "Place: " + latLng.latitude+ "\n" + latLng.longitude);
                Log.i(TAG, "Places" + place.getId() + ", " + place.getLatLng() + ", " + place.getName() + ", " + place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    // ---------------------------- Get user information -------------------------------------------
    private void currentLoggedUserInformation(){
        FirebaseUser user = mAuth.getInstance().getCurrentUser(); //Check if no matters with mAuth instead of FirebaseUser
        //mMail = findViewById(R.id.user_mail);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        mName = headerView.findViewById(R.id.user_name);
        mMail = headerView.findViewById(R.id.user_mail);
        mPhoto = headerView.findViewById(R.id.user_photo);


        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            String testPhoto = "https://i.redd.it/wuaak297y4a21.jpg";

            if (name != null){
                mName.setText(name);
            }
            if (email != null){
                mMail.setText(email);
            }
            if (photoUrl == null){

                Glide.with(this)
                        .load(testPhoto)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhoto);
            }
        }


    }

    // ---------------------------- Lateral navigation menu ----------------------------------------
    public void navigationViewMenu(){
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(lateralNavListener);
    }

    public NavigationView.OnNavigationItemSelectedListener lateralNavListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_lunch:
                    Toasty.success(MainActivity.this, "Click on menu icon", Toasty.LENGTH_SHORT).show();
                    break;
                case R.id.nav_settings:
                    Toasty.success(MainActivity.this, "Click on settings icon", Toasty.LENGTH_SHORT).show();
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent backToLogin = new Intent(MainActivity.this, LoginRegisterActivity.class);
                    startActivity(backToLogin);
                    Toasty.success(MainActivity.this, "Logout with success", Toasty.LENGTH_SHORT).show();
                    break;

            }
            return false;
        }
    };
}
