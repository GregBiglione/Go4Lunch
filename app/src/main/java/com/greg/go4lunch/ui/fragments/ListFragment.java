package com.greg.go4lunch.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.greg.go4lunch.R;
import com.greg.go4lunch.adapters.RestaurantAdapter;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.ui.activities.DetailedRestaurant;
import com.greg.go4lunch.event.DetailedRestaurantEvent;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;

public class ListFragment extends Fragment {

    @BindView(R.id.restaurant_recycler_view) RecyclerView mRestaurantRecyclerView;
    private RestaurantAdapter mRestaurantAdapter;
    List<Restaurant> restaurants;
    private SharedViewModel mSharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRestaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);
        return view;
    }

    // ---------------------------- Get all restaurants --------------------------------------------
    private void initList() {
        restaurants = mSharedViewModel.getRestaurants();
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRestaurantRecyclerView.setAdapter(new RestaurantAdapter(restaurants, Places.createClient(getContext()), getContext()));
    }

    //----------------------------- Go to detailed restaurant --------------------------------------
    @Subscribe
    @AllowConcurrentEvents
    public void onDetailedRestaurant(DetailedRestaurantEvent event){
        try {
            Intent i = new Intent(getContext(), DetailedRestaurant.class);
            i.putExtra("RestaurantDetails", Parcels.wrap(event.restaurant));
            startActivity(i);
        } catch(Exception e){
            Log.d("ListFragment", "Parcelable is too big");
        }
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

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }
}
