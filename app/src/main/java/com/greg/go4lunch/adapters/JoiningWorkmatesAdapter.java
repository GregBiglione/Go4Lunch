package com.greg.go4lunch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greg.go4lunch.R;
import com.greg.go4lunch.model.Workmate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class JoiningWorkmatesAdapter extends RecyclerView.Adapter<JoiningWorkmatesAdapter.ViewHolder> {

    private List<Workmate> mJoiningWorkmates;

    public JoiningWorkmatesAdapter(List<Workmate> mJoiningWorkmates) {
        this.mJoiningWorkmates = mJoiningWorkmates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.joining_workmate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(mJoiningWorkmates.get(position));
        Glide.with(holder.mJoiningCircleImageView.getContext())
                .load(mJoiningWorkmates.get(position).getPicture())
                .into(holder.mJoiningCircleImageView);
        holder.mJoiningMessageTextView.setText(mJoiningWorkmates.get(position).getName() + " is joining!");
    }

    @Override
    public int getItemCount() {
        return mJoiningWorkmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.joining_workmates_photo) CircleImageView mJoiningCircleImageView;
        @BindView(R.id.joining_workmate_message) TextView mJoiningMessageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
