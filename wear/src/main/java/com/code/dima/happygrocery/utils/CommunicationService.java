package com.code.dima.happygrocery.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.activity.DashboardActivity;
import com.code.dima.happygrocery.activity.HomeActivity;
import com.code.dima.happygrocery.activity.ProductDetailsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

public class CommunicationService extends WearableListenerService {

    public static void getNodeAtStart(final Context context) {
        final ConnectedNode connectedNode = ConnectedNode.getInstance();
        System.out.println("Wearable: trying to connect with phone");
        Task<CapabilityInfo> getPhone = Wearable.getCapabilityClient(context).getCapability(DataPaths.WATCH_SERVER, CapabilityClient.FILTER_REACHABLE);
        getPhone.addOnSuccessListener(new OnSuccessListener<CapabilityInfo>() {
            @Override
            public void onSuccess(CapabilityInfo capabilityInfo) {
                for (Node node: capabilityInfo.getNodes()) {
                    connectedNode.setConnectedNodeID(node.getId());
                }
                if (connectedNode.getConnectedNodeID() != null) {
                    // notify the home activity that I connected to a node
                    Intent capabilityIntent = new Intent(DataPaths.ACTION_CONNECTED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(capabilityIntent);
                } else {
                    // there's no node to connect to
                    Intent capabilityIntent = new Intent(DataPaths.ACTION_DISCONNECTED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(capabilityIntent);
                }
            }
        });
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        String message = "Received capability changed event";
        Intent notificationIntent = new Intent(DataPaths.ACTION_NOTIFICATION);
        notificationIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
        String capabilityName = capabilityInfo.getName();
        ConnectedNode connectedNode = ConnectedNode.getInstance();
        if (capabilityName.equals(DataPaths.WATCH_SERVER)) {
            // I found the list of phones I can connect to
            Set<Node> nodes = capabilityInfo.getNodes();
            ArrayList<String> nodeIDs = new ArrayList<>();
            for (Node node: nodes) {
                nodeIDs.add(node.getId());
            }
            if(connectedNode.getConnectedNodeID() != null && ! nodeIDs.contains(connectedNode.getConnectedNodeID())) {
                // I was connected to a phone which is no longer reachable
                // -> if I am in the Dashboard, I must exit from it
                Intent returnHome = new Intent(this, HomeActivity.class);
                String result = getString(R.string.connection_lost);
                returnHome.putExtra("result", result);
                startActivity(returnHome);
            }
            if(connectedNode.getConnectedNodeID() == null || ! (nodeIDs.contains(connectedNode.getConnectedNodeID()))) {
                // I was connected to a phone which is no longer reachable or
                // I was not connected to any phone -> select a new one
                for (String id : nodeIDs) {
                    connectedNode.setConnectedNodeID(id);
                }
                if (connectedNode.getConnectedNodeID() != null) {
                    // notify the home activity that I connected to a node
                    Intent capabilityIntent = new Intent(DataPaths.ACTION_CONNECTED);
                    LocalBroadcastManager.getInstance(this).sendBroadcastSync(capabilityIntent);
                } else {
                    // there's no node to connect to
                    Intent capabilityIntent = new Intent(DataPaths.ACTION_DISCONNECTED);
                    LocalBroadcastManager.getInstance(this).sendBroadcastSync(capabilityIntent);
                }
            }
        }
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        String message = "Received data changed event";
        System.out.println(message);
        Intent notificationIntent = new Intent(DataPaths.ACTION_NOTIFICATION);
        notificationIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);

        for(DataEvent event: dataEventBuffer) {
            if(event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                String path = item.getUri().getPath();
                if(DataPaths.AMOUNT_PATH.equals(path)) {
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                    float amount = map.getFloat(DataPaths.AMOUNT_KEY);
                    Intent amountNotification = new Intent(DataPaths.ACTION_UPDATE_AMOUNT);
                    amountNotification.putExtra("amount", amount);
                    LocalBroadcastManager.getInstance(this).sendBroadcastSync(amountNotification);
                } else if (DataPaths.QUANTITIES_PATH.equals(path)) {
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                    String quantities = map.getString(DataPaths.QUANTITIES_KEY);
                    Intent quantitiesNotification = new Intent(DataPaths.ACTION_UPDATE_QUANTITIES);
                    quantitiesNotification.putExtra("quantities", quantities);
                    LocalBroadcastManager.getInstance(this).sendBroadcastSync(quantitiesNotification);
                }
            }
            else {
                Log.d("Wearable", "Perceived unknown event");
            }
        }
    }
}
