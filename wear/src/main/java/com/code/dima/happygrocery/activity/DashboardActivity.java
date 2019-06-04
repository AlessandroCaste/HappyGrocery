package com.code.dima.happygrocery.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.wear.ambient.AmbientModeSupport;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.utils.Category;
import com.code.dima.happygrocery.utils.DashboardNotificationReceiver;
import com.code.dima.happygrocery.utils.DataPaths;
import com.code.dima.happygrocery.utils.InitializeImageRetrieverTask;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends WearableActivity
        implements AmbientModeSupport.AmbientCallbackProvider {

    private PieChart chart;
    private List<String> labels;
    private List<Integer> colors;

    private DashboardNotificationReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeChart();

        // initialize the image retriever for notifications on new products added to the cart
        new InitializeImageRetrieverTask(this).execute();

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new DashboardNotificationReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DataPaths.ACTION_UPDATE_AMOUNT);
        filter.addAction(DataPaths.ACTION_UPDATE_QUANTITIES);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        receiver = null;
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


    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
    }
}
