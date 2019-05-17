package com.code.dima.happygrocery.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wear.ambient.AmbientModeSupport;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.utils.Category;
import com.code.dima.happygrocery.utils.DataPaths;
import com.code.dima.happygrocery.utils.InitializeImageRetrieverTask;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends WearableActivity
        implements AmbientModeSupport.AmbientCallbackProvider,
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{

    private PieChart chart;
    private List<String> labels;
    private List<Integer> colors;

    private String phoneNodeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        phoneNodeId = getIntent().getStringExtra("phoneId");

        initializeChart();

        // initialize the image retriever for notifications on new products added to the cart
        new InitializeImageRetrieverTask(this).execute();

        // Enables Always-on
        setAmbientEnabled();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Instantiates clients without member variables, as clients are inexpensive to create and
        // won't lose their listeners. (They are cached and shared between GoogleApi instances.)
        Wearable.getDataClient(this).addListener(this);
        Wearable.getMessageClient(this).addListener(this);
        Wearable.getCapabilityClient(this).addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
    }


    @Override
    protected void onPause() {
        super.onPause();

        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);
    }


    private void initializeChart() {
        chart = findViewById(R.id.wear_pie_chart);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(R.color.dark_grey);
        chart.setHoleRadius(75);
        //chart.setCenterTextColor(R.color.white);
        chart.setCenterTextColor(R.color.light_grey);
        chart.setCenterTextSize(36);
        Description desc = new Description();
        desc.setText("10.0$");
        chart.setDescription(desc);
        chart.setDrawEntryLabels(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        chart.setTouchEnabled(true);

        colors = new ArrayList<>();
        labels = new ArrayList<>();
        for (Category category: Category.values()) {
            String label = category.name();
            labels.add(label);
            colors.add(Category.getCategoryColor(label));
        }

        List<Integer> valueList = new ArrayList<>();
        for (int i = 0; i < labels.size(); i ++) {
            valueList.add(0);
        }
        updateAmount(0f);
        updateChartEntries(valueList);
    }


    private void updateAmount(float amount) {
        String amountString = getResources().getString(R.string.currency) + String.format("%.2f", amount);
        chart.setCenterText(amountString);
        chart.invalidate();
    }


    private void updateChartEntries(List<Integer> valueList) {
        // sets values and colors for the entries in the PieChart
        ArrayList<PieEntry> yValues = new ArrayList<>();
        PieEntry newEntry;
        for (int i = 0; i < valueList.size(); i++) {
            newEntry = new PieEntry(valueList.get(i), i);
            newEntry.setLabel(labels.get(i));
            yValues.add(newEntry);
        }
        PieDataSet dataset = new PieDataSet(yValues, "Categories");
        dataset.setSliceSpace(3);
        dataset.setSelectionShift(4);
        dataset.setColors(colors);
        dataset.setDrawValues(false);
        chart.setData(new PieData(dataset));
        chart.invalidate();
    }


    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        // Note: here my "local node" is always set, it can only disconnect
        String capabilityName = capabilityInfo.getName();
        if (capabilityName.equals("watch_server")) {
            ArrayList<String> ids = new ArrayList<>();
            for (Node node : capabilityInfo.getNodes()) {
                ids.add(node.getId());
            }
            if(!(ids.contains(phoneNodeId))) {
                // I was connected to a phone which is no longer reachable
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        }
    }


    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        Log.d("Wearable", "Data changed: " + dataEventBuffer);

        for(DataEvent event: dataEventBuffer) {
            if(event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                String path = item.getUri().getPath();
                if(DataPaths.AMOUNT_PATH.equals(path)) {
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                    float amount = map.getFloat(DataPaths.AMOUNT_KEY);
                    updateAmount(amount);
                } else if (DataPaths.QUANTITIES_PATH.equals(path)) {
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                    String quantities = map.getString(DataPaths.QUANTITIES_KEY);
                    // quantities is the form: "1,2,3,4,5,6"
                    ArrayList<Integer> quantityPerCategory = new ArrayList<>();
                    for (String toc: quantities.split(",")) {
                        quantityPerCategory.add(Integer.parseInt(toc));
                    }
                    updateChartEntries(quantityPerCategory);
                }
            }
            else {
                Log.d("Wearable", "Perceived unknown event");
            }
        }
    }


    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        /* useful to be able to:
            1) receive message for the addition of a new product
            2) send request to view the grocery to the phone
         */
        if(messageEvent.getSourceNodeId().equals(phoneNodeId)) {
            String path = messageEvent.getPath();
            if (path.equals(DataPaths.NOTIFY_NEW_PRODUCT)) {
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
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }


    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
    }
}
