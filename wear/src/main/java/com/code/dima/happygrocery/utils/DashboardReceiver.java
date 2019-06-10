package com.code.dima.happygrocery.utils;

import android.net.Uri;

import androidx.annotation.NonNull;
import com.code.dima.happygrocery.activity.DashboardActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class DashboardReceiver
        implements MessageClient.OnMessageReceivedListener,
            DataClient.OnDataChangedListener,
            CapabilityClient.OnCapabilityChangedListener {

    private String connectedNodeID;
    private WeakReference<DashboardActivity> dashboard;

    public DashboardReceiver(DashboardActivity dashboard, String connectedNodeID) {
        this.connectedNodeID = connectedNodeID;
        this.dashboard = new WeakReference<>(dashboard);
    }

    public void resumeGrocery() {
        Task<DataItem> amountTask = Wearable.getDataClient(dashboard.get()).getDataItem(
                Uri.fromParts("wear", "*", DataPaths.AMOUNT_PATH)
        );
        amountTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                updateAmount(dataItem);
            }
        });
        Task<DataItem> quantityTask = Wearable.getDataClient(dashboard.get()).getDataItem(
                Uri.fromParts("wear", "*", DataPaths.QUANTITIES_PATH)
        );
        quantityTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                updateQuantities(dataItem);
            }
        });
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        System.out.println("Dashboard Activity - Received capability changed event");
        ArrayList<String> nodeIDs = new ArrayList<>();
        for(Node node: capabilityInfo.getNodes()) {
            nodeIDs.add(node.getId());
        }
        if(! nodeIDs.contains(connectedNodeID)) {
            dashboard.get().returnHome();
        }
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        System.out.println("Dashboard Activity - Received a data changed event");
        for(DataEvent event: dataEventBuffer) {
            if(event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                String path = item.getUri().getPath();
                if(DataPaths.AMOUNT_PATH.equals(path)) {
                    System.out.println("Data changed event - updated amount");
                    updateAmount(item);
                } else if (DataPaths.QUANTITIES_PATH.equals(path)) {
                    System.out.println("Data changed event - updated quantities");
                    updateQuantities(item);
                }
            } else {
                System.out.println("Data changed event - unknown event type");
            }
        }
    }

    private void updateAmount(DataItem item) {
        DataMap map = DataMapItem.fromDataItem(item).getDataMap();
        float amount = map.getFloat(DataPaths.AMOUNT_KEY);
        dashboard.get().updateAmount(amount);
    }

    private void updateQuantities(DataItem item) {
        DataMap map = DataMapItem.fromDataItem(item).getDataMap();
        String quantities = map.getString(DataPaths.QUANTITIES_KEY);
        // quantities is in the form: "1,2,3,4,5,6"
        ArrayList<Integer> quantityPerCategory = new ArrayList<>();
        for (String toc: quantities.split(",")) {
            quantityPerCategory.add(Integer.parseInt(toc));
        }
        dashboard.get().updateChartEntries(quantityPerCategory);
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if(messageEvent.getSourceNodeId().equals(connectedNodeID)) {
            System.out.println("Dashboard Activity - Received a message event");
            String path = messageEvent.getPath();
            if (path.equals(DataPaths.NOTIFY_NEW_PRODUCT)) {
                // a new product has been added to the cart
                // details encoded as a string "name;category;quantity;price"
                String details = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                String[] splits = details.split(";");
                dashboard.get().showNewProduct(Arrays.asList(splits));
            } else if (path.equals(DataPaths.NOTIFY_GROCERY_CLOSED)) {
                // the grocery has been closed correctly
                dashboard.get().closeGrocery();
            } else if (path.equals(DataPaths.NOTIFY_GROCERY_CLEARED)) {
                dashboard.get().returnHome();
            }
        }
    }
}
