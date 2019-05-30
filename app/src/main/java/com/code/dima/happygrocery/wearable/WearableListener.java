package com.code.dima.happygrocery.wearable;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.code.dima.happygrocery.R;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WearableListener implements MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    private WeakReference<Context> context;
    private boolean fromDashboard;

    public WearableListener(Context context, boolean fromDashboard) {
        this.context = new WeakReference<>(context);
        this.fromDashboard = fromDashboard;
        Wearable.getMessageClient(context).addListener(this);
        Wearable.getCapabilityClient(context).addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
    }

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
                String message = context.get().getString(R.string.wearable_disconnected);
                Toast.makeText(context.get(), message, Toast.LENGTH_SHORT).show();
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
                String message = context.get().getString(R.string.wearable_connected);
                Toast.makeText(context.get(), message, Toast.LENGTH_SHORT).show();
                if (fromDashboard) {
                    // notify the wearable to start a new grocery
                    CommunicationHandler.getInstance().notifyNewGrocery(context.get());
                }
            }
        }
    }

    public void disconnect() {
        Wearable.getMessageClient(context.get()).removeListener(this);
        Wearable.getCapabilityClient(context.get()).removeListener(this);
    }
}
