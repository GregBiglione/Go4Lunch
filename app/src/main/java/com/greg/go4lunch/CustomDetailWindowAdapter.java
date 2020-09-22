package com.greg.go4lunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomDetailWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomDetailWindowAdapter(Context mContext) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_detail_window, null);
    }

    public void addTextIntoWindow(Marker marker, View view){
        String name = marker.getTitle();
        TextView tvName = view.findViewById(R.id.restaurant_name_window);

        if (!name.equals("")){
            tvName.setText(name);
        }

        String details = marker.getSnippet();
        TextView tvDetails = view.findViewById(R.id.restaurant_details_window);

        if (!details.equals("")){
            tvDetails.setText(details);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        addTextIntoWindow(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        addTextIntoWindow(marker, mWindow);
        return mWindow;
    }
}
