package com.greg.go4lunch.ui.main_activity;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
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
import com.greg.go4lunch.ui.login.LoginRegisterActivity;
import com.greg.go4lunch.ui.settings.SettingActivity;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    public BottomNavigationView mBottomNavigationView;
    public static final String API_KEY = BuildConfig.ApiKey;
    PlacesClient mPlacesClient;

    public static final String TAG = "MainActivity";

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @BindView(R.id.user_name)
    TextView mName;
    @BindView(R.id.user_mail)
    TextView mMail;
    @BindView(R.id.user_photo)
    ImageView mPhoto;

    public NavigationView mNavigationView;
    private SharedViewModel mSharedViewModel;

    NavController navController;
    @BindView(R.id.autocomplete_recycler_view)
    RecyclerView mAutocompleteRecyclerView;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLocation;

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
        //configureAutocompleteRecyclerView();
        createLocationService();
        locationAccuracy();
        //configureSearchBar();
        //searchBarAction();
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

    public void initPlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }
        mPlacesClient = Places.createClient(this);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Bottom Navigation Menu -----------------------------------------
    //----------------------------------------------------------------------------------------------

    public void navigationBottomMenu() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_maps:
                    navController.navigate(R.id.nav_home);
                    break;
                case R.id.nav_list:
                    navController.navigate(R.id.nav_list);
                    break;
                case R.id.nav_workmates:
                    navController.navigate(R.id.nav_workmates);
                    break;
            }
            return true;
        }
    };

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get user information -------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void getCurrentUserFromFireBase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            //String photoEmail = user.getPhotoUrl().toString();
            Uri photo = Uri.parse(String.valueOf(user.getPhotoUrl()));
            Uri anonymous = Uri.parse("https://avante.biz/wp-content/uploads/Imagenes-De-Anonymous-Wallpapers/Imagenes-De-Anonymous-Wallpapers-001.jpg");

            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            mName = headerView.findViewById(R.id.user_name);
            mMail = headerView.findViewById(R.id.user_mail);
            mPhoto = headerView.findViewById(R.id.user_photo);

            if (name != null) {
                mName.setText(name);
            }
            if (email != null) {
                mMail.setText(email);
            }
            if (photo != null || !photo.equals("null")) {
                Glide.with(MainActivity.this)
                        .load(photo)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhoto);
            } else {
                Glide.with(MainActivity.this)
                        .load(anonymous)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhoto);
            }
        }
    }

    // ---------------------------- Get current user -----------------------------------------------
    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //----------------------------------------------------------------------------------------------
    // ---------------------------- Lateral navigation menu ----------------------------------------
    //----------------------------------------------------------------------------------------------

    public void navigationViewMenu() {
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(lateralNavListener);
    }

    public NavigationView.OnNavigationItemSelectedListener lateralNavListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_lunch:
                    goToMyLunchRestaurant();
                    break;
                case R.id.nav_settings:
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

    private void setUpFireBaseListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                    Toasty.success(MainActivity.this, getString(R.string.logout_with_success), Toasty.LENGTH_SHORT).show();
                    clearUserLoggedInfo();
                }
            }
        };
    }

    //----------------------------- Clear user information -----------------------------------------
    private void clearUserLoggedInfo() {
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
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Go to your selected restaurant ---------------------------------
    //----------------------------------------------------------------------------------------------

    private void goToMyLunchRestaurant() {
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                String idRestaurant = currentWorkmate.getIdPickedRestaurant();
                for (Restaurant r : mSharedViewModel.getRestaurants()) {
                    if (r.getIdRestaurant().equals(idRestaurant)) {
                        Intent goToMyRestaurantForLunch = new Intent(MainActivity.this, DetailedRestaurant.class);
                        goToMyRestaurantForLunch.putExtra("RestaurantDetails", Parcels.wrap(r));
                        startActivity(goToMyRestaurantForLunch);
                    } else if (idRestaurant == null) {
                        Toasty.warning(MainActivity.this, getString(R.string.no_restaurant_selected), Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void configureViewModel() {
        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Go to setting activity -----------------------------------------
    //----------------------------------------------------------------------------------------------

    private void goToSetting() {
        Intent goToSetting = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(goToSetting);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure autocomplete recyclerview ----------------------------
    //----------------------------------------------------------------------------------------------

    private void configureAutocompleteRecyclerView() {
        mAutocompleteRecyclerView = findViewById(R.id.autocomplete_recycler_view);
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, mLocation);
        mAutocompleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutocompleteRecyclerView.setAdapter(mAutoCompleteAdapter);
    }

    //----------------------------- Location service -----------------------------------------------
    private void createLocationService() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void locationAccuracy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLocation = location;
                    configureAutocompleteRecyclerView();
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Search view ----------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_restaurants));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.equals("")){
                    if (s.length() >= 3){
                        //mAutocompleteRecyclerView.setVisibility(View.VISIBLE);
                        mAutoCompleteAdapter.getFilter().filter(s);
                        mAutocompleteRecyclerView.setAdapter(mAutoCompleteAdapter);
                        //searchBarAction();
                    }
                    else{
                        //mAutocompleteRecyclerView.setVisibility(View.GONE);
                        mAutocompleteRecyclerView.setAdapter(null);
                    }
                }
                return true;
            }
        });
        return true;
    }


    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure Search bar -------------------------------------------
    //----------------------------------------------------------------------------------------------

    //private void configureSearchBar(){
    //    mSearchAutocomplete = findViewById(R.id.autocomplete_search_bar);
    //    mSearchAutocomplete.addTextChangedListener(filterTextWatcher);
    //}

    /// Doesn't work
    //private void searchBarAction(){
    //    mSearchAutocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
    //        @Override
    //        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    //            if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
    //                    || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
    //                // Execute search method
    //                //hideKeyboard();
    //                // Shown clicking enter on PC keyboard
    //                Toasty.success(MainActivity.this, "Click on item search bar message", Toasty.LENGTH_SHORT).show();
    //            }
    //            return false;
    //        }
    //    });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Search Menu ----------------------------------------------------
    //----------------------------------------------------------------------------------------------

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.search_menu, menu);
    //    return true;
    //}
//
    //@Override
    //public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    //    View autocompleteSearchBar = findViewById(R.id.autocomplete_linear_layout);
    //    if (item.getItemId() == R.id.search) {
    //        autocompleteSearchBar.setVisibility(View.VISIBLE);
    //        //hideKeyboard();
    //        //configureSearchBar();
    //    } else {
    //        autocompleteSearchBar.setVisibility(View.GONE);
    //    }
    //    return super.onOptionsItemSelected(item);
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Hide search bar when not focused -------------------------------
    //----------------------------------------------------------------------------------------------

    //private void hideKeyboard() {
    //    mSearchAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    //        @Override
    //        public void onFocusChange(View v, boolean hasFocus) {
    //            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    //            View autocompleteSearchBar = findViewById(R.id.autocomplete_linear_layout);
    //            if (!hasFocus) {
    //                imm.hideSoftInputFromWindow(autocompleteSearchBar.getWindowToken(), 0);
    //                autocompleteSearchBar.setVisibility(View.GONE);
    //                mAutocompleteRecyclerView.setVisibility(View.GONE);
    //            }
    //        }
    //    });
    //    mAutocompleteRecyclerView.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    //            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    //            mAutocompleteRecyclerView.setVisibility(View.GONE);
    //        }
    //    });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Hide search bar and Recycler view clicking on item -------------
    //----------------------------------------------------------------------------------------------

    //private void hideKeyboardAndRecyclerViewOnclick(){
    //    mAutocompleteRecyclerView.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    //            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    //        }
    //    });
    //}

}