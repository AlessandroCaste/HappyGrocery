package com.code.dima.happygrocery.utils;

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

    private String connectedNodeID;

    @Override
    public void onCreate() {
        super.onCreate();
        Task<CapabilityInfo> getPhone = Wearable.getCapabilityClient(this).getCapability(DataPaths.WATCH_SERVER, CapabilityClient.FILTER_REACHABLE);
        getPhone.addOnSuccessListener(new OnSuccessListener<CapabilityInfo>() {
            @Override
            public void onSuccess(CapabilityInfo capabilityInfo) {
                String capabilityName = capabilityInfo.getName();
                if (capabilityName.equals(DataPaths.WATCH_SERVER)) {
                    // I found the list of phones I can connect to
                    Set<Node> nodes = capabilityInfo.getNodes();
                    ArrayList<String> nodeIDs = new ArrayList<>();
                    for (Node node: nodes) {
                        nodeIDs.add(node.getId());
                    }
                    for (String id : nodeIDs) {
                        connectedNodeID = id;
                    }
                    if (connectedNodeID != null) {
                        // notify the home activity that I connected to a node
                        Intent capabilityIntent = new Intent(DataPaths.ACTION_CONNECTED);
                        LocalBroadcastManager.getInstance(CommunicationService.this).sendBroadcast(capabilityIntent);

                        // send a message to the phone to connect to
                        Wearable.getMessageClient(getApplicationContext()).sendMessage(
                                connectedNodeID, DataPaths.NOTIFY_CONNECTED, null);
                    } else {
                        // there's no node to connect to
                        Intent capabilityIntent = new Intent(DataPaths.ACTION_DISCONNECTED);
                        LocalBroadcastManager.getInstance(CommunicationService.this).sendBroadcast(capabilityIntent);
                    }
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
        if (capabilityName.equals(DataPaths.WATCH_SERVER)) {
            // I found the list of phones I can connect to
            Set<Node> nodes = capabilityInfo.getNodes();
            ArrayList<String> nodeIDs = new ArrayList<>();
            for (Node node: nodes) {
                nodeIDs.add(node.getId());
            }
            if(connectedNodeID != null && ! nodeIDs.contains(connectedNodeID)) {
                // I was connected to a phone which is no longer reachable
                // -> if I am in the Dashboard, I must exit from it
                Intent returnHome = new Intent(this, HomeActivity.class);
                String result = getString(R.string.connection_lost);
                returnHome.putExtra("result", result);
                startActivity(returnHome);
            }
            if(connectedNodeID == null || ! (nodeIDs.contains(connectedNodeID))) {
                // I was connected to a phone which is no longer reachable or
                // I was not connected to any phone -> select a new one
                for (String id : nodeIDs) {
                    connectedNodeID = id;
                }
                if (connectedNodeID != null) {
                    // notify the home activity that I connected to a node
                    Intent capabilityIntent = new Intent(DataPaths.ACTION_CONNECTED);
                    LocalBroadcastManager.getInstance(this).sendBroadcastSync(capabilityIntent);

                    // send a message to the phone to connect to
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(
                            connectedNodeID, DataPaths.NOTIFY_CONNECTED, null);
                } else {
                    // there's no node to connect to
                    Intent capabilityIntent = new Intent(DataPaths.ACTION_DISCONNECTED);
                    LocalBroadcastManager.getInstance(this).sendBroadcastSync(capabilityIntent);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        String message = "Received message event";
        System.out.println(message);
        Intent notificationIntent = new Intent(DataPaths.ACTION_NOTIFICATION);
        notificationIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
        if(messageEvent.getSourceNodeId().equals(connectedNodeID)) {
            String path = messageEvent.getPath();
            if(path.equals(DataPaths.NOTIFY_NEW_GROCERY)) {
                Intent startDashboard = new Intent(this, DashboardActivity.class);
                startDashboard.putExtra("phoneID", connectedNodeID);
                startActivity(startDashboard);
            } else if (path.equals(DataPaths.NOTIFY_NEW_PRODUCT)) {
                // a new product has been added to the cart
                // details encoded as a string "name;category;quantity;price"
                String details = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                String[] splits = details.split(";");
                try {
                    Intent displayDetailsIntent = new Intent(this, ProductDetailsActivity.class);
                    displayDetailsIntent.putExtra("name", splits[0]);
                    displayDetailsIntent.putExtra("category", splits[1]);
                    displayDetailsIntent.putExtra("quantity", splits[2]);
                    displayDetailsIntent.putExtra("price", splits[3]);
                    startActivity(displayDetailsIntent);
                } catch (Exception e) {
                    Log.e("Wearable", "Received bad formatted details for new added product");
                }
            } else if (path.equals(DataPaths.NOTIFY_GROCERY_CLOSED)) {
                // the grocery has been closed correctly
                Intent startHome = new Intent(this, HomeActivity.class);
                String resultOk = getString(R.string.grocery_closed);
                startHome.putExtra("result", resultOk);
                startActivity(startHome);
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
