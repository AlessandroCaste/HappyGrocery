package com.code.dima.happygrocery.wearable;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.code.dima.happygrocery.R;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

public class CommunicationService extends WearableListenerService {

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        CommunicationHandler handler = CommunicationHandler.getInstance();
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
                String message = getString(R.string.wearable_disconnected);
                sendBroadcastIntent(message);
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        CommunicationHandler handler = CommunicationHandler.getInstance();
        String connectedNodeID = handler.getConnectedNodeID();
        if(connectedNodeID == null) {
            if (messageEvent.getPath().equals(DataPaths.NOTIFY_CONNECTED)) {
                handler.setConnectedNodeID(messageEvent.getSourceNodeId());
                Log.d("Communication service", "Connected to " + messageEvent.getSourceNodeId());
                // sends a local broadcast intent in order for the activities to notify the connection to the user
                String message = getString(R.string.wearable_connected);
                sendBroadcastIntent(message);
            }
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void sendBroadcastIntent(String message) {
        Intent notificationIntent = new Intent(DataPaths.ACTION_NOTIFICATION);
        notificationIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
    }
}
