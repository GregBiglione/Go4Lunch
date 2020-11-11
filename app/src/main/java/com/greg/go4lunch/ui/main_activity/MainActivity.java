package com.greg.go4lunch.ui.main_activity;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.greg.go4lunch.BuildConfig;
import com.greg.go4lunch.R;
import com.greg.go4lunch.adapters.PlacesAutoCompleteAdapter;
import com.greg.go4lunch.api.WorkmateHelper;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.ui.detailled_restaurant.DetailedRestaurant;
import com.greg.go4lunch.ui.home.HomeFragment;
import com.greg.go4lunch.ui.login.LoginRegisterActivity;
import com.greg.go4lunch.ui.settings.SettingActivity;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public BottomNavigationView mBottomNavigationView;
    public static final String API_KEY = BuildConfig.ApiKey;
    PlacesClient mPlacesClient;

    public static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @BindView(R.id.user_name) TextView mName;
    @BindView(R.id.user_mail) TextView mMail;
    @BindView(R.id.user_photo) ImageView mPhoto;

    public NavigationView mNavigationView;
    private HomeFragment mHomeFragment;
    private SharedViewModel mSharedViewModel;
    private DetailedRestaurant detailedRestaurant;

    @BindView(R.id.autocomplete_search_bar) EditText mSearchAutocomplete;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 17.0f;
    private StringBuilder mResult;

    NavController navController;
    @BindView(R.id.autocomplete_recycler_view) RecyclerView mAutocompleteRecyclerView;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        configureViewModel();
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_list, R.id.nav_workmates)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationBottomMenu();
        initPlaces();

                                       //  Twitter ok
        getCurrentUserFromFireBase();  //  Email pics not shown
        //getUserFromFireStore();     //   Google ok
                                      //   Fb pics not shown
        navigationViewMenu();
        setUpFireBaseListener();
        mSearchAutocomplete = findViewById(R.id.autocomplete_search_bar);
        mAutocompleteRecyclerView = findViewById(R.id.autocomplete_recycler_view);
        configureAutocompleteRecyclerView();
        configureSearchBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Search Menu ----------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        View autocompleteSearchBar = findViewById(R.id.autocomplete_linear_layout);
        if (item.getItemId() == R.id.search) {
            autocompleteSearchBar.setVisibility(View.VISIBLE);
            searchBarNotFocused();
        } else {
            autocompleteSearchBar.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Hide search bar when not focused -------------------------------
    //----------------------------------------------------------------------------------------------

    private void searchBarNotFocused(){
        mSearchAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View autocompleteSearchBar = findViewById(R.id.autocomplete_linear_layout);
                if (!hasFocus){
                    imm.hideSoftInputFromWindow(autocompleteSearchBar.getWindowToken(), 0);
                    autocompleteSearchBar.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get places -----------------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void initPlaces(){
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), API_KEY);
        }
        mPlacesClient = Places.createClient(this);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Bottom Navigation Menu -----------------------------------------
    //----------------------------------------------------------------------------------------------

    public void navigationBottomMenu(){
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            switch (item.getItemId()){
                case R.id.nav_maps:
                    //getSupportActionBar().setTitle(getString(R.string.ImHungry));
                    navController.navigate(R.id.nav_home);
                    break;
                case R.id.nav_list:
                    //getSupportActionBar().setTitle(getString(R.string.ImHungry));
                    navController.navigate(R.id.nav_list);
                    break;
                case R.id.nav_workmates:
                    //getSupportActionBar().setTitle(getString(R.string.AvailableWorkmates));
                    navController.navigate(R.id.nav_workmates);
                    break;
            }

            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment)
            //        .commit();
            return true;
        }
    };

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get user information -------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void getCurrentUserFromFireBase(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            //String photo = user.getPhotoUrl().toString();
            Uri photo = Uri.parse(String.valueOf(user.getPhotoUrl()));
            Uri anonymous =  Uri.parse("https://avante.biz/wp-content/uploads/Imagenes-De-Anonymous-Wallpapers/Imagenes-De-Anonymous-Wallpapers-001.jpg");


            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            mName = headerView.findViewById(R.id.user_name);
            mMail = headerView.findViewById(R.id.user_mail);
            mPhoto = headerView.findViewById(R.id.user_photo);

            if(name != null){
                mName.setText(name);
            }
            if (email != null){
                mMail.setText(email);
            }
            if (photo != null){
                Glide.with(MainActivity.this)
                        .load(photo)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhoto);
            }
            else{
                //Glide.with(MainActivity.this)
                //        .load(anonymous)
                //        .apply(RequestOptions.circleCropTransform())
                //        .into(mPhoto);
                mPhoto.setImageURI(anonymous);
            }
        }
    }

    // ---------------------------- Get current user -----------------------------------------------
    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    //----------------------------------------------------------------------------------------------
    // ---------------------------- Lateral navigation menu ----------------------------------------
    //----------------------------------------------------------------------------------------------

    public void navigationViewMenu(){
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(lateralNavListener);
    }

    public NavigationView.OnNavigationItemSelectedListener lateralNavListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_lunch:
                    goToMyLunchRestaurant();
                    break;
                case R.id.nav_settings:
                    //Toasty.success(MainActivity.this, getString(R.string.language_changed), Toasty.LENGTH_SHORT).show();
                    //openLanguagesDialog();
                    //openSettingsDialog();
                    //upDateSharedPreferences();
                    goToSetting();
                    break;
                case R.id.nav_logout:
                    logOut();
                    Toasty.success(MainActivity.this, getString(R.string.logout_with_success), Toasty.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    };

    //----------------------------------------------------------------------------------------------
    //----------------------------- Log out --------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private void setUpFireBaseListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed in:" + user.getUid());
                }
                else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                    Toasty.success(MainActivity.this, getString(R.string.logout_with_success), Toasty.LENGTH_SHORT).show();
                    clearUserLoggedInfo();
                }
            }
        };
    }

    //----------------------------- Clear user information -----------------------------------------
    private void clearUserLoggedInfo(){
        Intent goToLogin = new Intent(MainActivity.this, LoginRegisterActivity.class);
        goToLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToLogin);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Go to your selected restaurant ---------------------------------
    //----------------------------------------------------------------------------------------------

    private void goToMyLunchRestaurant(){
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                String idRestaurant = currentWorkmate.getIdPickedRestaurant();
                for (Restaurant r: mSharedViewModel.getRestaurants()) {
                    if (r.getIdRestaurant().equals(idRestaurant)){
                        Intent goToMyRestaurantForLunch = new Intent(MainActivity.this, DetailedRestaurant.class);
                        goToMyRestaurantForLunch.putExtra("RestaurantDetails", Parcels.wrap(r));
                        startActivity(goToMyRestaurantForLunch);
                    }
                    else if (idRestaurant == null){
                        Toasty.warning(MainActivity.this, getString(R.string.no_restaurant_selected), Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void configureViewModel(){
        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Go to setting activity -----------------------------------------
    //----------------------------------------------------------------------------------------------

    private void goToSetting(){
        Intent goToSetting = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(goToSetting);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure autocomplete recyclerview ----------------------------
    //----------------------------------------------------------------------------------------------

    private void configureAutocompleteRecyclerView() {
        //mAutocompleteRecyclerView = findViewById(R.id.autocomplete_recycler_view);
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, mMap);
        //mAutocompleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutocompleteRecyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Autocomplete recyclerview visibility ---------------------------
    //----------------------------------------------------------------------------------------------

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")){
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (mAutocompleteRecyclerView.getVisibility() == View.GONE){
                    if (s.length() >= 3){
                        mAutocompleteRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    if(mAutocompleteRecyclerView.getVisibility() == View.VISIBLE){
                        mAutocompleteRecyclerView.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure Search bar -------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void configureSearchBar(){
        mSearchAutocomplete.addTextChangedListener(filterTextWatcher);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Move Camera to search location ---------------------------------
    //----------------------------------------------------------------------------------------------

    private void moveCameraToSearchedRestaurant(LatLng latLng, float zoom, String title){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        BitmapDescriptor subwayBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange);
        mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(subwayBitmapDescriptor)
                .title(title));
    }

    // ---------------------------- Language selection ---------------------------------------------
    private void openLanguagesDialog() {
        LanguagesDialog languagesDialog = new LanguagesDialog();
        languagesDialog.show(getSupportFragmentManager(),"languages dialog");
    }
}
