package com.code.dima.happygrocery.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.activity.HomeActivity;

import java.lang.ref.WeakReference;

public class HomeNotificationReceiver extends BroadcastReceiver {

    private WeakReference<HomeActivity> activity;

    public HomeNotificationReceiver(HomeActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(DataPaths.ACTION_CONNECTED)) {
                activity.get().displayText(R.string.connected);
            } else if (action.equals(DataPaths.ACTION_DISCONNECTED)) {
                activity.get().displayText(R.string.connection_lost);
            }
        }
    }
}
