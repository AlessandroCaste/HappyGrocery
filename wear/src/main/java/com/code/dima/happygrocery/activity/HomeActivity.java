package com.code.dima.happygrocery.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wear.ambient.AmbientModeSupport;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.utils.DataPaths;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

public class HomeActivity extends WearableActivity
        implements AmbientModeSupport.AmbientCallbackProvider,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    private TextView text;
    private Node phoneNode;
    private final int DASHBOARD_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        text = findViewById(R.id.home_activity_text);

        // performs an initial getCapabilities(), the further ones will be detected by the listener
        Task<CapabilityInfo> capabilityTask = Wearable.getCapabilityClient(this).
                getCapability(DataPaths.WATCH_SERVER, CapabilityClient.FILTER_REACHABLE);
        capabilityTask.addOnSuccessListener(new OnSuccessListener<CapabilityInfo>() {
            @Override
            public void onSuccess(CapabilityInfo capabilityInfo) {
                Set<Node> nodes = capabilityInfo.getNodes();
                connectToBestNode(nodes);
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Instantiates clients without member variables, as clients are inexpensive to create and
        // won't lose their listeners. (They are cached and shared between GoogleApi instances.)
        Wearable.getMessageClient(this).addListener(this);
        Wearable.getCapabilityClient(this).addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        //Wearable.getCapabilityClient(this).addListener(this, Uri.parse("wear://*/watch_server"), CapabilityClient.FILTER_REACHABLE);
        //Wearable.getCapabilityClient(this).addLocalCapability("watch_client");
    }


    @Override
    protected void onPause() {
        super.onPause();

        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        String capabilityName = capabilityInfo.getName();
        if (capabilityName.equals(DataPaths.WATCH_SERVER)) {
            // I found the list of phones I can connect to
            Set<Node> nodes = capabilityInfo.getNodes();
            if(phoneNode == null || ! (nodes.contains(phoneNode))) {
                // I was connected to a phone which is no longer reachable or
                // I was not connected to any phone -> select a new one
                connectToBestNode(nodes);
            }
        }
    }

    private void connectToBestNode(Set<Node> nodes) {
        if (nodes.size() > 0) {
            Log.d("Wearable", "Found a list of" + nodes.size() + "phones to be connected to");
            // TODO: select the best node to be connected to
            for (Node node : nodes) {
                phoneNode = node;
            }
            text.setText(R.string.connected);
            // I notify the phone and change the label next
            notifyConnectedPhone();
        } else {
            text.setText(R.string.connecting);
        }
    }

    private void notifyConnectedPhone() {
        Task<Integer> sendTask = Wearable.getMessageClient(this).sendMessage(
          phoneNode.getId(), DataPaths.NOTIFY_CONNECTED, null);
        sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer id) {
                Log.d("Wearable", "Notified node " + id + " of the connection");
                text.setText(R.string.waiting);
            }
        });
    }


    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        // fired when the phone starts a new grocery
        if(messageEvent.getPath().equals(DataPaths.NOTIFY_NEW_GROCERY)) {
            Intent launchDashboardIntent = new Intent(this, DashboardActivity.class);
            launchDashboardIntent.putExtra("phoneId", phoneNode.getId());
            startActivityForResult(launchDashboardIntent, DASHBOARD_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DASHBOARD_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                text.setText(R.string.grocery_closed);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                text.setText(R.string.connection_lost);
            }
        }
    }


    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
    }

}
