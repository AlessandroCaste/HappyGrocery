package com.code.dima.happygrocery.utils;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.code.dima.happygrocery.activity.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

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
            home.get().startDashboard(messageEvent.getSourceNodeId(), false);
        }
    }

    public void checkHandheldState() {
        Task<CapabilityInfo> capabilityTask = Wearable.getCapabilityClient(home.get()).getCapability(
                DataPaths.WATCH_SERVER, CapabilityClient.FILTER_REACHABLE);
        capabilityTask.addOnSuccessListener(new OnSuccessListener<CapabilityInfo>() {
            @Override
            public void onSuccess(CapabilityInfo capabilityInfo) {
                String nodeId = null;
                for (Node node : capabilityInfo.getNodes()) {
                    nodeId = node.getId();
                }
                checkResume(nodeId);
            }
        });
    }

    private void checkResume(String nodeID) {
        // get data map -> check inDashboard -> if true, launch dashboard
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("wear");
        builder.authority(nodeID);
        builder.path(DataPaths.IN_DASHBOARD_PATH);
        Uri uri = builder.build();
        Task<DataItem> task = Wearable.getDataClient(home.get()).getDataItem(uri);
        task.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                DataMap map = DataMapItem.fromDataItem(dataItem).getDataMap();
                String nodeID = dataItem.getUri().getHost();
                boolean inDashboard = map.getBoolean(DataPaths.IN_DASHBOARD_KEY);
                if(inDashboard) {
                    home.get().startDashboard(nodeID, true);
                }
            }
        });
    }
}
