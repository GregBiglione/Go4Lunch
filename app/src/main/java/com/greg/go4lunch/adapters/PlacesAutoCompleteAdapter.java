package com.greg.go4lunch.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.greg.go4lunch.R;
import com.greg.go4lunch.event.SearchRestaurantEvent;
import com.greg.go4lunch.model.Restaurant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.ViewHolder> implements Filterable {

    public static final String TAG = "PlacesAutoCompleteAdapter";
    private ArrayList<RestaurantAutocomplete> mAutocompleteRestaurant = new ArrayList<>();

    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private final PlacesClient mPlacesClient;
    private Location mLocation;
    private final Float radius = 100.0f;
    private Filter mFilter;

    public PlacesAutoCompleteAdapter(Context mContext, Location location) {
        this.mContext = mContext;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        mPlacesClient = com.google.android.libraries.places.api.Places.createClient(mContext);
        this.mLocation = location;

        this.mFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                //--------- Skip the autocomplete query if no constraints are given ----------------
                if (constraint != null){
                    //----- Query the autocomplete API for the (constraint) search string ----------
                    List predictions = getPredictions(constraint);
                    results.count = predictions.size();
                    results.values = predictions;

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0){
                    mAutocompleteRestaurant = (ArrayList<RestaurantAutocomplete>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Geo Data Autocomplete API results ------------------------------
    //----------------------------------------------------------------------------------------------

    public class RestaurantAutocomplete {
        public String restaurantId, name, address;

        public RestaurantAutocomplete(String restaurantId, String name, String address) {
            this.restaurantId = restaurantId;
            this.name = name;
            this.address = address;
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Autocomplete prediction ----------------------------------------
    //----------------------------------------------------------------------------------------------

    @SuppressLint("LongLogTag")
    private ArrayList<RestaurantAutocomplete> getPredictions(CharSequence constraint){
        final ArrayList<RestaurantAutocomplete> resultList = new ArrayList<>();
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(getRectangularBoundsA(), getRectangularBoundsB());
        // Use the builder to create a FindAutocompletePredictionsRequest. And Pass this to FindAutocompletePredictionsRequest,
        // when the user makes a selection (for example when calling fetchPlace()).
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictionsResponse = mPlacesClient.findAutocompletePredictions(request);
        // Block and wait for at most 60s for a result from the API
        try{
           Tasks.await(autocompletePredictionsResponse, 60, TimeUnit.SECONDS);
        }catch(ExecutionException | InterruptedException | TimeoutException e){
            e.printStackTrace();
        }

        if (autocompletePredictionsResponse.isSuccessful()){
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictionsResponse.getResult();
            if (findAutocompletePredictionsResponse != null){

                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)){
                        resultList.add(new RestaurantAutocomplete(prediction.getPlaceId(),
                                prediction.getPrimaryText(STYLE_NORMAL).toString(),
                                prediction.getFullText(STYLE_BOLD).toString()));
                    }
                }
                return resultList;
            }
        }
        return resultList;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Filter for the current set of autocomplete results -------------
    //----------------------------------------------------------------------------------------------

    public Filter getFilter(){
        return mFilter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = (String) mAutocompleteRestaurant.get(position).name;
        String address = (String) mAutocompleteRestaurant.get(position).address;
        holder.mAutocompleteRestaurantName.setText(name);
        holder.mAutocompleteRestaurantAddress.setText(address);

        holder.mAutoCompleteRelativeLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //----------------------------- Get LatLng ---------------------------------
                String restaurantId = mAutocompleteRestaurant.get(position).restaurantId;
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES,
                        Place.Field.LAT_LNG);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(restaurantId, placeFields).build();
                mPlacesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng latLng = place.getLatLng();
                        Restaurant r = new Restaurant();
                        r.setName(name);
                        r.setAddress(address);
                        r.setLatLng(latLng);
                        Toasty.success(mContext, "Click on" + name + "\n" + address + "\n" + latLng, Toasty.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new SearchRestaurantEvent(r));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException){
                            Toasty.error(mContext, e.getMessage() + "", Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAutocompleteRestaurant.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        @BindView(R.id.autocomplete_restaurant_name) TextView mAutocompleteRestaurantName;
        @BindView(R.id.autocomplete_restaurant_address) TextView mAutocompleteRestaurantAddress;
        @BindView(R.id.autocomplete_relative_Lyt) RelativeLayout mAutoCompleteRelativeLyt;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Calculate bounds ---------------------------------------
    //----------------------------------------------------------------------------------------------

    public LatLng getRectangularBoundsA(){
        double latA = mLocation.getLatitude() - (radius/111.321f);
        double lngA = mLocation.getLongitude() - (float) (radius/(111.321f * Math.cos(latA * (Math.PI/180.0f))));
        LatLng pointA = new LatLng(latA, lngA);

        return pointA;
    }

    public LatLng getRectangularBoundsB(){
        double latB = mLocation.getLatitude() + (radius/111.321f) ;
        double lngB = mLocation.getLongitude() + (float) (radius/(111.321f * Math.cos(latB * (Math.PI/180.0f))));
        LatLng pointB = new LatLng(latB, lngB);

        return pointB;
    }
}