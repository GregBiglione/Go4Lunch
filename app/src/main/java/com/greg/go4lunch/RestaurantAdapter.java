package com.greg.go4lunch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.ui.event.DetailedRestaurantEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants;

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Restaurant r = restaurants.get(position);

       holder.mRestaurantName.setText(r.getName());
       holder.mRestaurantAddress.setText(r.getAddress());
       holder.mRestaurantRating.setRating(r.getRating());
       //holder.mRestaurantHour.setText(r.getOpeningHour());

       Glide.with(holder.mRestaurantPicture)
               .load(r.getRestaurantPicture())
               .into(holder.mRestaurantPicture);

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
        @BindView(R.id.workmates_joining) TextView mJoiningWorkmates;
        @BindView(R.id.restaurant_hour) TextView mRestaurantHour;
        @BindView(R.id.restaurant_rating) RatingBar mRestaurantRating;
        @BindView(R.id.restaurant_picture) ImageView mRestaurantPicture;
        @BindView(R.id.button_to_detailed_restaurant) RelativeLayout mButtonDetailedRestaurant;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
