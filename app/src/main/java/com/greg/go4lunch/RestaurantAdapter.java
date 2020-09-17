package com.greg.go4lunch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greg.go4lunch.model.Restaurant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants;

    public RestaurantAdapter(List<Restaurant> mRestaurants) {
        this.mRestaurants = mRestaurants;
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
        holder.itemView.setTag(mRestaurants.get(position));
        holder.mRestaurantName.setText(mRestaurants.get(position).getName());
        holder.mRestaurantDistance.setText(mRestaurants.get(position).getDistanceFromUser());
        holder.mRestaurantAddress.setText(mRestaurants.get(position).getAddress());
        holder.mJoiningWorkmates.setText(mRestaurants.get(position).getJoiningNumber());
        holder.mRestaurantHour.setText(mRestaurants.get(position).getOpeningHour());
        holder.mRestaurantRating.setRating(mRestaurants.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.restaurant_name) TextView mRestaurantName;
        @BindView(R.id.restaurant_distance) TextView mRestaurantDistance;
        @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
        @BindView(R.id.workmates_joining) TextView mJoiningWorkmates;
        @BindView(R.id.restaurant_hour) TextView mRestaurantHour;
        @BindView(R.id.restaurant_rating) RatingBar mRestaurantRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
