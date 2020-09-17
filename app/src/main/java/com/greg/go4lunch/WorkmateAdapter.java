package com.greg.go4lunch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greg.go4lunch.model.Workmate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.ViewHolder> {

    private List<Workmate> mWorkmates;

    public WorkmateAdapter(List<Workmate> mWorkmates) {
        this.mWorkmates = mWorkmates;
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
        holder.mMessageTextView.setText(mWorkmates.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.workmates_photo)CircleImageView mCircleImageView;
        @BindView(R.id.workmate_isJoining_message) TextView mMessageTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
