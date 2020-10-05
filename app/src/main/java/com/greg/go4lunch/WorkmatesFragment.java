package com.greg.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greg.go4lunch.api.WorkmateHelper;
import com.greg.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class WorkmatesFragment extends Fragment {

    @BindView(R.id.workmates_recycler_view) RecyclerView mWorkmateRecyclerView;
    private WorkmateAdapter mWorkmateAdapater;
    List<Workmate> mWorkmates = new ArrayList<>();
    public static final String TAG = "WorkmateFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        mWorkmateRecyclerView = view.findViewById(R.id.workmates_recycler_view);
        mWorkmateRecyclerView.setHasFixedSize(true);
        mWorkmateRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        loadWorkmatesFromFireStore();
        mWorkmateRecyclerView.setAdapter(new WorkmateAdapter(mWorkmates));
        //setUpFireBase();
        return view;
    }



    // ---------------------------- Get All Workmates ----------------------------------------------
    private void loadWorkmatesFromFireStore() {
        //db.collection("workmates")
        //        .get()
        //        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        //            @Override
        //            public void onComplete(@NonNull Task<QuerySnapshot> task) {
        //                if (task.isSuccessful()){
        //                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
        //                        Workmate workmate = new Workmate(documentSnapshot.getString("uid"),
        //                                documentSnapshot.getString("picture"),
        //                                documentSnapshot.getString("name"),
        //                                documentSnapshot.getString("email"),
        //                                documentSnapshot.getString("pickedRestaurant"),
        //                                documentSnapshot.getBoolean("joining"));
        //                        mWorkmates.add(workmate);
        //                    }
        //                }
        //            }
        //        }).addOnFailureListener(new OnFailureListener() {
        //    @Override
        //    public void onFailure(@NonNull Exception e) {
        //        Toasty.error(getContext(), "Fail to load workmates", Toasty.LENGTH_SHORT).show();
        //    }
        //});


        //DocumentReference docRef = db.collection("workmayes").document();
        //docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        //    @Override
        //    public void onSuccess(DocumentSnapshot documentSnapshot) {
        //        Workmate workmate = documentSnapshot.toObject(Workmate.class);
        //        mWorkmates.add(workmate);
        //    }
        //});


        //db.collection("workmates")
        //        .get()
        //        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        //            @Override
        //            public void onComplete(@NonNull Task<QuerySnapshot> task) {
        //                if (task.isSuccessful()) {
        //                    for (QueryDocumentSnapshot document : task.getResult()) {
        //                        Log.d(TAG, document.getId() + " => " + document.getData());
        //                    }
        //                } else {
        //                    Log.d(TAG, "Error getting documents: ", task.getException());
        //                }
        //            }
        //        });
//
    }

}
