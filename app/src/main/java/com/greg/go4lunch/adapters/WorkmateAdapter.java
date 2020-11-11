package com.greg.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.greg.go4lunch.R;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.ui.detailled_restaurant.DetailedRestaurant;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.ViewHolder> {

    private List<Workmate> mWorkmates;
    private Context context;
    private SharedViewModel mSharedViewModel;

    public WorkmateAdapter(List<Workmate> mWorkmates, Context context) {
        this.mWorkmates = mWorkmates;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workmate_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(mWorkmates.get(position));
        Glide.with(holder.mCircleImageView.getContext())
                .load(mWorkmates.get(position).getPicture())
                .into(holder.mCircleImageView);
        if (mWorkmates.get(position) != null){

            String name = mWorkmates.get(position).getName();
            String restaurant = mWorkmates.get(position).getPickedRestaurant();
            String restaurantNotChosen = name + " " + context.getString(R.string.restaurantNotChosen);
            String restaurantChosen = name + " " + context.getString(R.string.restaurantChosen) + " " + restaurant;

            if(!mWorkmates.get(position).getJoining()){
                holder.mMessageTextView.setText(restaurantNotChosen);
                holder.mMessageTextView.setTextColor(ContextCompat.getColor(context, R.color.isJoiningColor));
                holder.mMessageTextView.setTypeface(holder.mMessageTextView.getTypeface(), Typeface.ITALIC);
            }
            else{
                holder.mMessageTextView.setText(restaurantChosen);
            }
        }

        holder.mButtonWorkmateToDetailedRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureViewModel();
                String idRestaurant = mWorkmates.get(position).getIdPickedRestaurant();
                if (idRestaurant != null){
                    for (Restaurant r: mSharedViewModel.getRestaurants()) {
                        if (r.getIdRestaurant().equals(idRestaurant)){
                            Intent goToMyRestaurantForLunch = new Intent(context, DetailedRestaurant.class);
                            goToMyRestaurantForLunch.putExtra("RestaurantDetails", Parcels.wrap(r));
                            context.startActivity(goToMyRestaurantForLunch);
                        }
                    }
                }
                else{
                    Toasty.warning(context, context.getString(R.string.no_restaurant_selected), Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configureViewModel(){
        mSharedViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SharedViewModel.class);
    }

    //----------------------------- Get current user -----------------------------------------------
    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.workmates_photo)CircleImageView mCircleImageView;
        @BindView(R.id.workmate_message) TextView mMessageTextView;
        @BindView(R.id.item_relative_Lyt) RelativeLayout mButtonWorkmateToDetailedRestaurant;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
