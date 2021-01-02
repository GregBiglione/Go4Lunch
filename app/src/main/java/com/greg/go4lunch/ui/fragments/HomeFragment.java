package com.greg.go4lunch.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.greg.go4lunch.R;
import com.greg.go4lunch.event.SearchRestaurantEvent;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.ui.activities.DetailedRestaurant;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.greg.go4lunch.ui.activities.MainActivity.API_KEY;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 17.0f;
    private static final String TAG = "HomeFragment";
    private static final String INTERNET = Manifest.permission.INTERNET;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    @BindView(R.id.gps) FloatingActionButton mGps;
    private PlacesClient mPlacesClient;
    private SharedViewModel mSharedViewModel;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationService();
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), API_KEY);
        }
        mPlacesClient = Places.createClient(getContext());
        configureViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mapView = view.findViewById(R.id.map);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        locationAccuracy();
        noLandMarksFilter(googleMap);
        checkPermissions();
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get last known location ----------------------------------------
    //----------------------------------------------------------------------------------------------

    //----------------------------- Location service -----------------------------------------------
    private void createLocationService(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @SuppressLint("MissingPermission")
    public void locationAccuracy(){
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mLocation = location;
                    LatLng losAngeles = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(losAngeles).title("I'm here and I'm hungry !"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(losAngeles));
                    zoomOnLocation();
                    getNearbyPlaces();
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Zoom level -----------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void zoomOnLocation(){
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- No Landmarks on Maps -------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void noLandMarksFilter(GoogleMap googleMap){
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Check permissions ----------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST_CODE)
    private void checkPermissions(){
        String[] perms = {ACCESS_FINE_LOCATION, INTERNET};
        if (EasyPermissions.hasPermissions(getContext(), perms)){
            Toasty.success(getContext(), getString(R.string.location_granted), Toasty.LENGTH_SHORT).show();
            customFocus();
        }
        else {
            EasyPermissions.requestPermissions(this,"We need your permission to locate you",
                    LOCATION_PERMISSION_REQUEST_CODE, perms);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Custom position enabled ----------------------------------------
    //----------------------------------------------------------------------------------------------

    private void customFocus(){
        mGps = getView().findViewById(R.id.gps);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationAccuracy();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //---------------------------- Places information type initialization --------------------------
    //----------------------------------------------------------------------------------------------

    public void getNearbyPlaces(){
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.LAT_LNG);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful()){
                        FindCurrentPlaceResponse response = task.getResult();
                        assert response != null;

                        final String placeId = response.getPlaceLikelihoods().get(0).getPlace().getId();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: '%f' ",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));

                            if (placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)){
                                Restaurant r = new Restaurant();
                                r.setIdRestaurant(placeLikelihood.getPlace().getId());
                                r.setName(placeLikelihood.getPlace().getName());
                                r.setAddress(placeLikelihood.getPlace().getAddress());
                                r.setLatLng(placeLikelihood.getPlace().getLatLng());
                                //----------------------------- Get distance to restaurant ---------
                                r.setDistanceFromUser(getDistance(r.getLatLng()));
                                //----------------------------- Custom marker & number joining workmates -----------
                                getJoiningWorkmateNumber(r);
                                if (!mSharedViewModel.restaurants.contains(r)){
                                    mSharedViewModel.restaurants.add(r);
                                }
                                getRestaurantDetails(r);
                                clickOnMarker();
                            }
                        }
                    }
                    else{
                        Exception exception = task.getException();
                        if (exception instanceof ApiException){
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                }
            });
        }
        else{
            checkPermissions();
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get restaurants details ----------------------------------------
    //----------------------------------------------------------------------------------------------

    private void getRestaurantDetails(Restaurant r){
        // ---------------------------- Define a place Id ------------------------------------------
        final String placeId = r.getIdRestaurant();

        // ---------------------------- Specify a field to return ----------------------------------
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS, Place.Field.RATING, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.PHOTO_METADATAS);

        // ---------------------------- Construct a request object, passing the place ID and fields array -----------------
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        mPlacesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                Log.i(TAG, "Place found: " + place.getName());
                if (place.getOpeningHours() != null){
                    r.setOpeningHour(place.getOpeningHours().getPeriods().get(4).getClose().getTime().getHours());
                }
                if (place.getRating() != null){
                    r.setRating(place.getRating().floatValue());
                }
                r.setPhoneNumber(place.getPhoneNumber());
                if (place.getWebsiteUri() != null){
                    r.setWebsite(place.getWebsiteUri().toString());
                }
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);
                r.setRestaurantPicture(photoMetadata);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ApiException){
                    final ApiException apiException = (ApiException) e;
                    Log.e(TAG, "Place not found: " + e.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure view model -------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void configureViewModel(){
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get distance to restaurant -------------------------------------
    //----------------------------------------------------------------------------------------------

    private String getDistance(LatLng restaurantLocation){
        Location currentLocation = new Location("locationA");
        currentLocation.setLatitude(mLocation.getLatitude());
        currentLocation.setLongitude(mLocation.getLongitude());

        Location destination = new Location("locationB");
        destination.setLatitude(restaurantLocation.latitude);
        destination.setLongitude(restaurantLocation.longitude);

        double accurateDistance = currentLocation.distanceTo(destination);
        int distance= (int) Math.round(accurateDistance);
        return (distance + "m");
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Custom marker --------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void restaurantIsChosenOrNot(Restaurant r){
        if(r.getJoiningNumber() > 0){
            BitmapDescriptor subwayBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green);
            mMap.addMarker(new MarkerOptions().position(r.getLatLng())
                    .icon(subwayBitmapDescriptor)
                    .title(r.getName()));
        }
        else{
            BitmapDescriptor subwayBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange);
            mMap.addMarker(new MarkerOptions().position(r.getLatLng())
                    .icon(subwayBitmapDescriptor)
                    .title(r.getName()));
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Click on marker ------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void clickOnMarker(){
       mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
           @Override
           public boolean onMarkerClick(Marker marker) {
               String restaurantTitle = marker.getTitle();
               for (Restaurant r : mSharedViewModel.getRestaurants()) {
                   if (r.getName().equals(restaurantTitle)){
                       Intent goToDetailedRestaurant = new Intent(getContext(), DetailedRestaurant.class);
                       goToDetailedRestaurant.putExtra("RestaurantDetails", Parcels.wrap(r));
                       startActivity(goToDetailedRestaurant);
                   }
               }
               return false;
           }
       });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Number joining workmates ---------------------------------------
    //----------------------------------------------------------------------------------------------

    private void getJoiningWorkmateNumber(Restaurant r){
        mSharedViewModel.initJoiningWorkmates(getContext(), r.getIdRestaurant());
        mSharedViewModel.getJoiningWorkmatesData().observe(requireActivity(), new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(ArrayList<Workmate> workmates) {
                    r.setJoiningNumber(workmates.size());
                    restaurantIsChosenOrNot(r);
                    //async task in onSuccessListener to update real time
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Autocomplete search event --------------------------------------
    //----------------------------------------------------------------------------------------------

    @Subscribe
    public void onAutocompleteSearch(SearchRestaurantEvent event){
        moveCameraToSearchedRestaurant(event.restaurant.getLatLng(), DEFAULT_ZOOM, event.restaurant.getName(), event.restaurant);
        mSharedViewModel.restaurants.add(event.restaurant);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Move Camera to search location ---------------------------------
    //----------------------------------------------------------------------------------------------

    private void moveCameraToSearchedRestaurant(LatLng latLng, float zoom, String title, Restaurant r){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        restaurantIsChosenOrNot(r);
    }
}