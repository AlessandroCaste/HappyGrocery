package com.code.dima.happygrocery.wearable;

import android.content.Context;
import android.util.Log;

import com.code.dima.happygrocery.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommunicationHandler {

    private String connectedNodeID;
    private static CommunicationHandler instance;

    private CommunicationHandler(Context context) {
        super();
        instance = null;
        initializeNodeID(context);
    }

    public static CommunicationHandler getInstance(Context context) {
        if(instance == null) {
            instance = new CommunicationHandler(context);
        }
        return instance;
    }

    String getConnectedNodeID() {
        return connectedNodeID;
    }

    void setConnectedNodeID(String nodeID) {
        this.connectedNodeID = nodeID;
    }

    private void initializeNodeID(Context context) {
        if (connectedNodeID == null) {
            Task<CapabilityInfo> getWatch = Wearable.getCapabilityClient(context).getCapability(DataPaths.WATCH_CLIENT, CapabilityClient.FILTER_REACHABLE);
            getWatch.addOnSuccessListener(new OnSuccessListener<CapabilityInfo>() {
                @Override
                public void onSuccess(CapabilityInfo capabilityInfo) {
                    for (Node node: capabilityInfo.getNodes()) {
                        connectedNodeID = node.getId();
                    }
                    System.out.println("HAPPY GROCERY - connected to " + connectedNodeID);
                }
            });
        }
    }

    public void notifyNewGrocery(Context context) {
        if(connectedNodeID == null) {
            initializeNodeID(context);
        }
        if (connectedNodeID != null) {
            Task<Integer> sendTask = Wearable.getMessageClient(context).sendMessage(
                    connectedNodeID, DataPaths.NOTIFY_NEW_GROCERY, null);
            sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer id) {
                    Log.d("Connection handler", "Notified watch of the start of a new grocery");
                }
            });
        }
    }

    public void notifyNewProductAdded(Context context, Product product) {
        if(connectedNodeID == null) {
            initializeNodeID(context);
        }
        if (connectedNodeID != null) {
            // details encoded as a string "name;category;quantity;price"
            String dataString = product.getName() + ';' +
                    product.getCategory().name() + ';' +
                    product.getQuantity() + ';' +
                    product.getPrice();
            byte[] payload = dataString.getBytes(StandardCharsets.UTF_8);

            Task<Integer> sendTask = Wearable.getMessageClient(context).sendMessage(
                    connectedNodeID, DataPaths.NOTIFY_NEW_PRODUCT, payload);
            sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer id) {
                    Log.d("Connection service", "Notified watch of the insertion of a new product");
                }
            });
        }
    }

    public void notifyGroceryClosed(Context context) {
        if(connectedNodeID == null) {
            initializeNodeID(context);
        }
        if (connectedNodeID != null) {
            Task<Integer> sendTask = Wearable.getMessageClient(context).sendMessage(
                    connectedNodeID, DataPaths.NOTIFY_GROCERY_CLOSED, null);
            sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer id) {
                    Log.d("Connection service", "Notified watch of the closing of the grocery");
                }
            });
        }
    }

    public void updateWearableAmount(Context context, float amount) {
        if(connectedNodeID == null) {
            initializeNodeID(context);
        }
        if (connectedNodeID != null) {
            // sends the wearable the actual amount of the actual grocery
            PutDataMapRequest dataRequest = PutDataMapRequest.create(DataPaths.AMOUNT_PATH);
            dataRequest.getDataMap().putFloat(DataPaths.AMOUNT_KEY, amount);
            PutDataRequest request = dataRequest.asPutDataRequest();
            request.setUrgent();
            Task<DataItem> updateTask = Wearable.getDataClient(context).putDataItem(request);
            updateTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
                @Override
                public void onSuccess(DataItem dataItem) {
                    Log.d("Communication service", "Sent new amount to the wearable");
                }
            });
        }
    }

    public void updateWearableQuantities(Context context, List<Integer> quantities) {
        if(connectedNodeID == null) {
            initializeNodeID(context);
        }
        if(connectedNodeID != null) {
            StringBuilder data = new StringBuilder();
            if (quantities.size() > 0) {
                data.append(quantities.get(0));
            }
            for (int i = 1; i < quantities.size(); i++) {
                data.append(',');
                data.append(quantities.get(i));
            }
            // I send the string data in the form: "1,2,3,4,5,6"
            PutDataMapRequest dataRequest = PutDataMapRequest.create(DataPaths.QUANTITIES_PATH);
            dataRequest.getDataMap().putString(DataPaths.QUANTITIES_KEY, data.toString());
            PutDataRequest request = dataRequest.asPutDataRequest();
            request.setUrgent();
            Task<DataItem> updateTask = Wearable.getDataClient(context).putDataItem(request);
            updateTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
                @Override
                public void onSuccess(DataItem dataItem) {
                    Log.d("Communication service", "Sent new quantities to the wearable");
                }
            });
        }
    }
}
