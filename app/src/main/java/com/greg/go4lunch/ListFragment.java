package com.greg.go4lunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greg.go4lunch.model.Restaurant;

import java.util.List;

import butterknife.BindView;

public class ListFragment extends Fragment {

    @BindView(R.id.restaurant_recycler_view) RecyclerView mRestaurantRecyclerView;
    private RestaurantAdapter mRestaurantAdapter;
    List<Restaurant> mRestaurants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRestaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRestaurantRecyclerView.setAdapter(new RestaurantAdapter(mRestaurants));
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}
