package com.greg.go4lunch.event;

import android.content.Context;

import com.greg.go4lunch.model.Workmate;

public class ReloadWorkmatesEvent {
    public Context context;

    public ReloadWorkmatesEvent(Context context) {
        this.context = context;
    }
}
