package com.code.dima.happygrocery.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.activity.DashboardActivity;
import com.code.dima.happygrocery.activity.HomeActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DashboardNotificationReceiver extends BroadcastReceiver {

    private WeakReference<DashboardActivity> dashboard;

    public DashboardNotificationReceiver(DashboardActivity activity) {
        this.dashboard = new WeakReference<>(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if(action.equals(DataPaths.ACTION_UPDATE_AMOUNT)) {
                float amount = intent.getFloatExtra("amount", 0f);
                dashboard.get().updateAmount(amount);
            } else if (action.equals(DataPaths.ACTION_UPDATE_QUANTITIES)) {
                String quantities = intent.getStringExtra("quantities");
                // quantities is the form: "1,2,3,4,5,6"
                ArrayList<Integer> quantityPerCategory = new ArrayList<>();
                for (String toc: quantities.split(",")) {
                    quantityPerCategory.add(Integer.parseInt(toc));
                }
                dashboard.get().updateChartEntries(quantityPerCategory);
            }
        }
    }
}
