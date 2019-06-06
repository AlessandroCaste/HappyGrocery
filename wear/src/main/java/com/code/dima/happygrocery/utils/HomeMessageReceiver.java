package com.code.dima.happygrocery.utils;

import androidx.annotation.NonNull;

import com.code.dima.happygrocery.activity.HomeActivity;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;

import java.lang.ref.WeakReference;

public class HomeMessageReceiver implements MessageClient.OnMessageReceivedListener {

    private WeakReference<HomeActivity> home;

    public HomeMessageReceiver(HomeActivity activity) {
        this.home = new WeakReference<>(activity);
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        System.out.println("Received message from " + messageEvent.getSourceNodeId());
        String path = messageEvent.getPath();
        if (path.equals(DataPaths.NOTIFY_NEW_GROCERY)) {
            home.get().startDashboard(messageEvent.getSourceNodeId());
        }
    }
}
