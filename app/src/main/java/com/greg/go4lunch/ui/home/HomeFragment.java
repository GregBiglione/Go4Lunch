package com.greg.go4lunch.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.greg.go4lunch.R;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 17.0f;
    private static final String TAG = "HomeFragment";
    private static final String INTERNET = Manifest.permission.INTERNET;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    @BindView(R.id.gps) FloatingActionButton mGps;
    private PlacesClient mPlacesClient;
    private SharedViewModel mSharedViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlacesClient = Places.createClient(getContext());
        //mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
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
        zoomOnLocation();
        noLandMarksFilter(googleMap);
        checkPermissions();
    }

    // ---------------------------- Location accuracy ----------------------------------------------
    private void locationAccuracy(){
        LatLng santaMonica = new LatLng(34.017434, -118.491768);
        mMap.addMarker(new MarkerOptions().position(santaMonica).title("I'm here and I'm hungry !"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(santaMonica));
    }

    // ---------------------------- Zoom level -----------------------------------------------------
    private void zoomOnLocation(){
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    }

    // ---------------------------- No Landmarks on Maps -------------------------------------------
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

    // ---------------------------- Check permissions ----------------------------------------------
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
            getNearbyPlaces();
        }
        else {
            EasyPermissions.requestPermissions(this,"We need your permission to locate you",
                    LOCATION_PERMISSION_REQUEST_CODE, perms);
        }
    }

    // ---------------------------- Custom position enabled ----------------------------------------
    private void customFocus(){
        mGps = getView().findViewById(R.id.gps);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationAccuracy();
            }
        });
    }

    //---------------------------- Places information type initialization -------------------------
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
                                mSharedViewModel.restaurants.add(r);
                                //mMap.setInfoWindowAdapter(new CustomDetailWindowAdapter(getActivity()));
                                mMap.addMarker(new MarkerOptions().position(placeLikelihood.getPlace().getLatLng())
                                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                       .title(r.getName() + "\n" + r.getAddress()));
                                getRestaurantDetails(r);
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

    // ---------------------------- Get restaurants details ---------------------------------------------------------------------------------------------------
    private void getRestaurantDetails(Restaurant r){
        // ---------------------------- Define a place Id ----------------------------------------------
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
                r.setOpeningHour(place.getOpeningHours().toString());
                r.setRating(place.getRating().floatValue());
                r.setPhoneNumber(place.getPhoneNumber());
                r.setWebsite(place.getWebsiteUri().toString());
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);
                getRestaurantPhoto(r, photoMetadata);
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

    // ---------------------------- Get restaurants photo --------------------------------------------------------------------------------------------------------
    private void getRestaurantPhoto(Restaurant r, PhotoMetadata photoMetadata){

        // ---------------------------- Create a FetchPhotoRequest -------------------------
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .build();
        mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
            @Override
            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                r.setRestaurantPicture(bitmap);
                //imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    final ApiException apiException = (ApiException) e;
                    Log.e(TAG, "Place not found: " + e.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            }
        });
    }
}