package com.code.dima.happygrocery.wearable;

import android.content.Context;
import android.util.Log;

import com.code.dima.happygrocery.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommunicationHandler {

    private String connectedNodeID;
    private static CommunicationHandler instance;

    private CommunicationHandler() {
        super();
        this.connectedNodeID = null;
        instance = null;
    }

    public static CommunicationHandler getInstance() {
        if(instance == null) {
            instance = new CommunicationHandler();
        }
        return instance;
    }

    String getConnectedNodeID() {
        return connectedNodeID;
    }

    void setConnectedNodeID(String nodeID) {
        this.connectedNodeID = nodeID;
    }


    public void notifyNewGrocery(Context context) {
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
