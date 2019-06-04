package com.code.dima.happygrocery.wearable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.code.dima.happygrocery.R;

import java.lang.ref.WeakReference;

public class NotificationReceiver extends BroadcastReceiver {

    private WeakReference<Context> appContext;
    private boolean fromDashboard;

    public NotificationReceiver(Context context, boolean fromDashboard) {
        this.appContext = new WeakReference<>(context);
        this.fromDashboard = fromDashboard;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // As hypothesis, the receiver will be registered as to filter the only type of
        // filter action sent by the communication service
        String message = intent.getStringExtra("message");
        Toast.makeText(appContext.get(), message, Toast.LENGTH_SHORT).show();

        String connectedMessage = appContext.get().getString(R.string.wearable_connected);
        if (fromDashboard && message.equals(connectedMessage)) {
            // notify the wearable to start a new grocery
            CommunicationHandler.getInstance().notifyNewGrocery(appContext.get());
        }
    }
}
