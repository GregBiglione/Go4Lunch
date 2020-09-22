package com.greg.go4lunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.viewmodel.SharedViewModel;

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
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        initList();
        return view;
    }

    private void initList() {
        //restaurants = mSharedViewModel.restaurants;
        restaurants = mSharedViewModel.getRestaurants();
        mRestaurantRecyclerView.setAdapter(new RestaurantAdapter(restaurants));
    }

}
