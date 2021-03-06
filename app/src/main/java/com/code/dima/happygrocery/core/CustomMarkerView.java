package com.code.dima.happygrocery.core;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.code.dima.happygrocery.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;


public class CustomMarkerView extends MarkerView {

    private TextView text;
    private MPPointF offset;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        text = findViewById(R.id.markerTextView);
        PieChart chart = findViewById(R.id.DashboardPieChart);
        setChartView(chart);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        PieEntry entry = (PieEntry) e;
        int value = (int) entry.getY();
        String label = entry.getLabel();
        text.setText(value + " " + getResources().getString(R.string.marker_text) + " " + label);
        super.refreshContent(e,highlight);
    }

    @Override
    public MPPointF getOffset() {

        if(offset == null) {
            // center the marker horizontally and vertically
            offset = new MPPointF(-(getWidth() / 2), -getHeight() / 2);
        }

        return offset;
    }
}
