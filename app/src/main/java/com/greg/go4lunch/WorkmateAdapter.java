package com.greg.go4lunch;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greg.go4lunch.model.Workmate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.ViewHolder> {

    private List<Workmate> mWorkmates;
    private Context context;

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
    }

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
