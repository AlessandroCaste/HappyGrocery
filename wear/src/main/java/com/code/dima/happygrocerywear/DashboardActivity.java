package com.code.dima.happygrocerywear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class DashboardActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StaticOne.hello();

        setContentView(R.layout.activity_dashboard);

        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText("Ciaone speriamo funzioni 3");

        // Enables Always-on
        setAmbientEnabled();
    }
}
