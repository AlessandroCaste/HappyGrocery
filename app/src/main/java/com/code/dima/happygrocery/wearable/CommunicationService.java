package com.code.dima.happygrocery.wearable;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.code.dima.happygrocery.R;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

public class CommunicationService extends WearableListenerService {

    public static final String CONNECTION_NOTIFICATION = "notify_connection_from_wearable";
    public enum notificationType {
        CONNECTED,
        DISCONNECTED
    }

    private LocalBroadcastManager broadcastManager = null;
    private CommunicationHandler handler = CommunicationHandler.getInstance();

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String connectedNodeID = handler.getConnectedNodeID();
        if(connectedNodeID == null) {
            if (messageEvent.getPath().equals(DataPaths.NOTIFY_CONNECTED)) {
                handler.setConnectedNodeID(messageEvent.getSourceNodeId());
                Log.d("Communication service", "Connected to " + messageEvent.getSourceNodeId());
                // sends a local broadcast intent in order for the activities to notify the connection to the user
                sendLocalBroadcastNotification(notificationType.CONNECTED);
            }
        }
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        String capabilityName = capabilityInfo.getName();
        String connectedNodeID = handler.getConnectedNodeID();
        if (connectedNodeID != null &&
                capabilityName.equals(DataPaths.WATCH_CLIENT)) {
            ArrayList<String> nodes = new ArrayList<>();
            for(Node node : capabilityInfo.getNodes()) {
                nodes.add(node.getId());
            }
            if (! nodes.contains(connectedNodeID)) {
                // the watch I was connected to is no longer reachable
                Log.d("Communication service", "Lost connection with " + connectedNodeID);
                handler.setConnectedNodeID(null);
                sendLocalBroadcastNotification(notificationType.DISCONNECTED);
            }
        }
    }

    private void sendLocalBroadcastNotification(notificationType notification) {
        if (broadcastManager == null) {
            broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        }
        Intent notifyIntent = new Intent(CONNECTION_NOTIFICATION);
        if (notification == notificationType.CONNECTED) {
            notifyIntent.putExtra("message", R.string.wearable_connected);
        } else {
            notifyIntent.putExtra("message", R.string.wearable_disconnected);
        }
        broadcastManager.sendBroadcast(notifyIntent);
    }
}
