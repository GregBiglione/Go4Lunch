package com.greg.go4lunch;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.ui.DetailedRestaurant;
import com.greg.go4lunch.ui.event.DetailedRestaurantEvent;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants;
    private PlacesClient mPlacesClient;
    private Context c;
    private DetailedRestaurant mDetailedRestaurant;
    private SharedViewModel mSharedViewModel;
    private List<Workmate> mWorkmate;

    public RestaurantAdapter(List<Restaurant> restaurants, PlacesClient mPlacesClient, Context c) {
        this.restaurants = restaurants;
        this.mPlacesClient = mPlacesClient;
        this.c = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item, parent, false);
        //configureRecyclerView();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Restaurant r = restaurants.get(position);

       //String joiningWorkmates = String.valueOf(mJoiningWorkmatesAdapter.getItemCount());
       holder.mRestaurantName.setText(r.getName());
       holder.mRestaurantAddress.setText(r.getAddress());

       //mSharedViewModel.initJoiningWorkmates(c, r.getIdRestaurant());
       //mSharedViewModel.getJoiningWorkmatesData().observe((LifecycleOwner) c, new Observer<ArrayList<Workmate>>() {
       //    @Override
       //    public void onChanged(ArrayList<Workmate> workmates) {
       //        r.setJoiningNumber(workmates.size());
       //    }
       //});
        if (r.getJoiningNumber() > 0){
            holder.mJoiningWorkmates.setText("(" + r.getJoiningNumber() + ")");
            holder.mJoiningWorkmates.setVisibility(View.VISIBLE);
        }
        //holder.mJoiningWorkmates.setText("(" + r.getJoiningNumber() + ")");
        //holder.mJoiningWorkmates.setVisibility(View.VISIBLE);
       //holder.mJoiningWorkmates.setText("(" + r.getJoiningNumber() + ")");
       //holder.mJoiningWorkmates.setVisibility(View.VISIBLE);
        //mSharedViewModel.initJoiningWorkmates(c, r.getIdRestaurant());
        //mSharedViewModel.getJoiningWorkmatesData().observe((LifecycleOwner) c, new Observer<ArrayList<Workmate>>() {
        //    @Override
        //    public void onChanged(ArrayList<Workmate> workmates) {
//
        //    }
        //});
       holder.mRestaurantHour.setText(c.getString(R.string.close_hour) + " " + r.getOpeningHour() +"h");
       holder.mRestaurantRating.setRating(r.getRating());
       holder.mRestaurantDistance.setText(r.getDistanceFromUser());
       getRestaurantPhoto(holder.mRestaurantPicture, r.getRestaurantPicture());
       holder.mButtonDetailedRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new DetailedRestaurantEvent(r));
            }
       });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.restaurant_name) TextView mRestaurantName;
        @BindView(R.id.restaurant_distance) TextView mRestaurantDistance;
        @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
        @BindView(R.id.workmates_joining_number) TextView mJoiningWorkmates;
        @BindView(R.id.restaurant_hour) TextView mRestaurantHour;
        @BindView(R.id.restaurant_rating) RatingBar mRestaurantRating;
        @BindView(R.id.restaurant_picture) ImageView mRestaurantPicture;
        @BindView(R.id.button_to_detailed_restaurant) RelativeLayout mButtonDetailedRestaurant;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Configure view model -------------------------------------------
    //----------------------------------------------------------------------------------------------

    //public void configureRecyclerView(){
    //    mSharedViewModel.initJoiningWorkmates(c, restaurants.get(0).getIdRestaurant());
    //    mSharedViewModel.getJoiningWorkmatesData().observe((LifecycleOwner) c, new Observer<ArrayList<Workmate>>() {
    //        @Override
    //        public void onChanged(ArrayList<Workmate> workmates) {
    //            mWorkmate = workmates;
    //            workmates.size();
    //        }
    //    });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Get restaurants photo ------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void getRestaurantPhoto(ImageView v, PhotoMetadata photoMetadata){

        // ---------------------------- Create a FetchPhotoRequest -------------------------
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .build();
        mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
            @Override
            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                Glide.with(c)
                        .load(bitmap)
                        .into(v);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    final ApiException apiException = (ApiException) e;
                    final int statusCode = apiException.getStatusCode();
                }
            }
        });
    }


}
