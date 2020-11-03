package com.greg.go4lunch;

import android.content.Context;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;

import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.greg.go4lunch.api.WorkmateHelper;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.ui.DetailedRestaurant;
import com.greg.go4lunch.ui.home.HomeFragment;
import com.greg.go4lunch.ui.settings.SettingActivity;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    //----------------------------------------------------------------------------------------------
    private Workmate mWorkmate;
    private List<Restaurant> restaurants;

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
        getSupportActionBar().setTitle(getString(R.string.ImHungry));

        navigationBottomMenu();
        initPlaces();
        //autocompleteSupportFragInit();

                                       //  Twitter ok
        getCurrentUserFromFireBase();  //  Email pics not shown
        //getUserFromFireStore();     //   Google ok
                                      //   Fb pics not shown
        navigationViewMenu();
        setUpFireBaseListener();
        //clickOnAutocompleteSearchBar();
        mSearchAutocomplete = findViewById(R.id.autocomplete_search_bar);
        searchBarCustom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Hide search bar when not focused -------------------------------
    //----------------------------------------------------------------------------------------------

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
    //----------------------------- Autocomplete prediction ----------------------------------------
    //----------------------------------------------------------------------------------------------

    //private void clickOnAutocompleteSearchBar(){
    //    mSearchAutocomplete = findViewById(R.id.autocomplete_search_bar);
    //    // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest
    //    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
    //    // Create a RectangularBounds object.
    //    RectangularBounds bounds = RectangularBounds.newInstance(
    //            new LatLng(-33.880490, 151.184363),
    //            new LatLng(-33.858754, 151.229596)
    //    );
    //    // Use the builder to create a FindAutocompletePredictionsRequest. And Pass this to FindAutocompletePredictionsRequest,
    //    // when the user makes a selection (for example when calling fetchPlace()).
    //    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
    //            // Call either setLocationBias() OR setLocationRestriction().
    //            .setLocationBias(bounds)
    //            .setTypeFilter(TypeFilter.ESTABLISHMENT)
    //            .setSessionToken(token)
    //            .setQuery(mSearchAutocomplete.getText().toString())
    //            .build();
//
    //    mPlacesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
    //        @Override
    //        public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
    //            // adapter ??
    //        }
    //    }).addOnFailureListener(new OnFailureListener() {
    //        @Override
    //        public void onFailure(@NonNull Exception e) {
    //            if (e instanceof ApiException){
    //                ApiException apiException = (ApiException) e;
    //                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
    //            }
    //        }
    //    });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Search bar custom ----------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void searchBarCustom(){
        mSearchAutocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //searchRestaurant();
                }
                return false;
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Search restaurant with search bar ------------------------------
    //----------------------------------------------------------------------------------------------

    //public void searchRestaurant(){
    //    String searchRestaurant = mSearchAutocomplete.getText().toString();
//
    //    Geocoder geocoder = new Geocoder(MainActivity.this);
    //    List<Address> list = new ArrayList<>();
    //    try {
    //        list = geocoder.getFromLocationName(searchRestaurant, 1);
    //    }catch (IOException e){
    //        Log.e(TAG, "relocate: IOException" + e.getMessage());
    //    }
//
    //    if (list.size() > 0){
    //        Address address = list.get(0);
    //        Log.d(TAG, "Location seach in Search bar info:" + address.toString());
    //        LatLng latLngAddressRestaurant = new LatLng(address.getLatitude(), address.getLongitude());
    //        moveCameraToSearchedRestaurant(latLngAddressRestaurant, DEFAULT_ZOOM, address.getAddressLine(0));
    //    }
    //}
//
    ////----------------------------------------------------------------------------------------------
    ////----------------------------- Move Camera to search location ---------------------------------
    ////----------------------------------------------------------------------------------------------
//
    //private void moveCameraToSearchedRestaurant(LatLng latLng, float zoom, String title){
    //    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    //    BitmapDescriptor subwayBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange);
    //    mMap.addMarker(new MarkerOptions().position(latLng)
    //            .icon(subwayBitmapDescriptor)
    //            .title(title));
    //}


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
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_maps:
                    selectedFragment = new NavHostFragment();
                    getSupportActionBar().setTitle(getString(R.string.ImHungry));
                    break;
                case R.id.nav_list:
                    selectedFragment = new ListFragment();
                    getSupportActionBar().setTitle(getString(R.string.ImHungry));
                    break;
                case R.id.nav_workmates:
                    selectedFragment = new WorkmatesFragment();
                    getSupportActionBar().setTitle(getString(R.string.AvailableWorkmates));
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };

    //public void autoCompletePrediction(){
    //    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
//
    //    RectangularBounds bounds = RectangularBounds.newInstance(
    //            new LatLng(),
    //            new LatLng()
    //    );
//
    //    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
    //            .setLocationBias(bounds)
    //            .setTypeFilter(TypeFilter.ESTABLISHMENT)
    //            .setSessionToken(token)
    //            .setQuery()
    //            .build()
    //}


    // ---------------------------- Autocomplete support fragment initialization -------------------
    //public void autocompleteSupportFragInit(){
    //    AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager()
    //            .findFragmentById(R.id.autocomplete_fragment);
//
    //    autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));
//
    //    autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
    //        @Override
    //        public void onPlaceSelected(@NonNull Place place) {
    //            LatLng latLng = place.getLatLng();
    //            //Log.i(TAG, "Place: " + latLng.latitude+ "\n" + latLng.longitude);
    //            Log.i(TAG, getString(R.string.places) + place.getId() + ", " + place.getLatLng() + ", " + place.getName() + ", " + place.getAddress());
    //        }
//
    //        @Override
    //        public void onError(@NonNull Status status) {
    //            Log.i(TAG, getString(R.string.error_occurred) + status);
    //        }
    //    });
    //}

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
                    Toasty.success(MainActivity.this, "Click on menu icon", Toasty.LENGTH_SHORT).show();
                    //goToMyLunchRestaurant();
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

    //private void goToMyLunchRestaurant(){
    //    WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    //        @Override
    //        public void onSuccess(DocumentSnapshot documentSnapshot) {
    //            Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
    //            String idRestaurant = currentWorkmate.getIdPickedRestaurant();
    //            String nameRestaurant = currentWorkmate.getPickedRestaurant();
    //            String addressRestaurant = currentWorkmate.getAddressRestaurant();
    //            PhotoMetadata photoRestaurant =  currentWorkmate.getPhotoRestaurant();
    //            float ratingRestaurant = currentWorkmate.getRatingRestaurant();
    //            String websiteRestaurant = currentWorkmate.getWebsiteRestaurant();
    //            String phoneRestaurant = currentWorkmate.getPhoneRestaurant();
    //            LatLng latLngRestaurant = currentWorkmate.getLatLng();
    //            int joiningNumber = currentWorkmate.getJoiningNumber();
    //            int openingHour = currentWorkmate.getOpeningHour();
    //            if (currentWorkmate != null){
    //                Intent goToMyRestaurantForLunch = new Intent(MainActivity.this, DetailedRestaurant.class);
    //                Restaurant restaurant = new Restaurant(idRestaurant, nameRestaurant, addressRestaurant,
    //                        photoRestaurant, ratingRestaurant, websiteRestaurant, phoneRestaurant, latLngRestaurant, joiningNumber,
    //                        openingHour);
    //                goToMyRestaurantForLunch.putExtra("RestaurantDetails", Parcels.wrap(restaurant));
    //                startActivity(goToMyRestaurantForLunch);
    //            }
    //            else{
    //                Toasty.warning(getApplicationContext(), getString(R.string.no_restaurant_selected), Toasty.LENGTH_SHORT).show();
    //            }
    //        }
    //    });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Go to setting activity -----------------------------------------
    //----------------------------------------------------------------------------------------------

    private void goToSetting(){
        Intent goToSetting = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(goToSetting);
    }

    // ---------------------------- Language selection ---------------------------------------------
    private void openLanguagesDialog() {
        LanguagesDialog languagesDialog = new LanguagesDialog();
        languagesDialog.show(getSupportFragmentManager(),"languages dialog");
    }
}
