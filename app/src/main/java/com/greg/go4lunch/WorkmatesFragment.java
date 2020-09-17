package com.greg.go4lunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greg.go4lunch.model.Workmate;

import java.util.List;

import butterknife.BindView;

public class WorkmatesFragment extends Fragment {

    @BindView(R.id.workmates_recycler_view) RecyclerView mWorkmateRecyclerView;
    private WorkmateAdapter mWorkmateAdapater;
    List<Workmate> mWorkmates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        mWorkmateRecyclerView = view.findViewById(R.id.workmates_recycler_view);
        mWorkmateRecyclerView.setHasFixedSize(true);
        mWorkmateRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mWorkmateRecyclerView.setAdapter(new WorkmateAdapter(mWorkmates));
        return view;
    }

}
