package com.code.dima.happygrocery.wearable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ConnectionBroadcastReceiver extends BroadcastReceiver {

    private WeakReference<Context> activityContext;
    private boolean fromDashboard;

    public ConnectionBroadcastReceiver(Context context, boolean fromDashboard) {
        activityContext = new WeakReference<>(context);
        this.fromDashboard = fromDashboard;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        Log.d("Happy Grocery", message);
        Toast.makeText(activityContext.get(), message, Toast.LENGTH_SHORT).show();
        if (fromDashboard) {
            // notify the wearable to start a new grocery
            CommunicationHandler.getInstance().notifyNewGrocery(activityContext.get());
        }
    }
}
