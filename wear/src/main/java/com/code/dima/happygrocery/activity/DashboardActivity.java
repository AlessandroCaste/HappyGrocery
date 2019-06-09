package com.code.dima.happygrocery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import androidx.wear.ambient.AmbientModeSupport;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.utils.Category;
import com.code.dima.happygrocery.utils.DashboardReceiver;
import com.code.dima.happygrocery.utils.DataPaths;
import com.code.dima.happygrocery.utils.InitializeImageRetrieverTask;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends WearableActivity
        implements AmbientModeSupport.AmbientCallbackProvider {

    private PieChart chart;
    private List<String> labels;
    private List<Integer> colors;

    private DashboardReceiver receiver;
    private String connectedNodeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        connectedNodeID = getIntent().getStringExtra("phoneID");

        initializeChart();

        // initialize the image retriever for notifications on new products added to the cart
        new InitializeImageRetrieverTask(this).execute();

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new DashboardReceiver(this, connectedNodeID);
        Wearable.getMessageClient(this).addListener(receiver);
        Wearable.getCapabilityClient(this).addListener(receiver, DataPaths.WATCH_SERVER);
        Wearable.getDataClient(this).addListener(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Wearable.getMessageClient(this).removeListener(receiver);
        Wearable.getCapabilityClient(this).removeListener(receiver);
        Wearable.getDataClient(this).removeListener(receiver);
        receiver = null;
    }


    private void initializeChart() {
        chart = findViewById(R.id.wear_pie_chart);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(getColor(R.color.black));
        chart.setHoleRadius(75);
        chart.setDrawCenterText(true);
        chart.setCenterTextColor(getColor(R.color.light_grey));
        chart.setCenterTextSize(36);
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setDrawEntryLabels(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.invalidate();

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


    public void updateAmount(float amount) {
        String amountString = getResources().getString(R.string.currency) + String.format("%.2f", amount);
        chart.setCenterText(amountString);
        chart.invalidate();
    }


    public void updateChartEntries(List<Integer> valueList) {
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

    public void showNewProduct(List<String> details) {
        if(details.size() == 4) {
            Intent displayDetailsIntent = new Intent(this, ProductDetailsActivity.class);
            displayDetailsIntent.putExtra("name", details.get(0));
            displayDetailsIntent.putExtra("category", details.get(1));
            displayDetailsIntent.putExtra("quantity", details.get(2));
            displayDetailsIntent.putExtra("price", details.get(3));
            startActivity(displayDetailsIntent);
        } else {
            System.out.println("Dashboard Activity - Received a bad formatted product details");
        }
    }

    public void closeGrocery() {
        setResult(RESULT_OK);
        finish();
    }

    public void returnHome() {
        setResult(RESULT_CANCELED);
        finish();
    }


    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
    }
}
