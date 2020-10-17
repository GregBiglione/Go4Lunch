package com.greg.go4lunch;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import java.util.ArrayList;

import butterknife.BindView;

public class WorkmatesFragment extends Fragment {

    @BindView(R.id.workmates_recycler_view) RecyclerView mWorkmateRecyclerView;
    private WorkmateAdapter mWorkmateAdapter;
    private SharedViewModel mSharedViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mSharedViewModel.initAllWorkmates(getContext());

        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        mWorkmateRecyclerView = view.findViewById(R.id.workmates_recycler_view);
        //configureRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        configureRecyclerView();
    }

    private void configureRecyclerView(){
        mWorkmateRecyclerView.setHasFixedSize(true);
        mWorkmateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSharedViewModel.getAllWorkmatesData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(ArrayList<Workmate> workmates) {
                mWorkmateAdapter = new WorkmateAdapter(workmates, getContext());
                mWorkmateRecyclerView.setAdapter(mWorkmateAdapter);
                mWorkmateAdapter.notifyDataSetChanged();
            }
        });
    }
}

