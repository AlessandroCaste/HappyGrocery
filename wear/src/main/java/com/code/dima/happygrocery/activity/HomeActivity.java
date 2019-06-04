package com.code.dima.happygrocery.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.wear.ambient.AmbientModeSupport;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.utils.DataPaths;
import com.code.dima.happygrocery.utils.HomeNotificationReceiver;


public class HomeActivity extends WearableActivity
        implements AmbientModeSupport.AmbientCallbackProvider {

    private HomeNotificationReceiver receiver;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        text = findViewById(R.id.home_activity_text);

        String result = getIntent().getStringExtra("result");
        if (result != null) {
            text.setText(result);
        } else {
            text.setText(R.string.waiting);
        }


        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new HomeNotificationReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DataPaths.ACTION_CONNECTED);
        filter.addAction(DataPaths.ACTION_DISCONNECTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        receiver = null;
    }

    public void displayText(String string) {
        text.setText(string);
    }

    public void displayText(int stringID) {
        text.setText(stringID);
    }


    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
    }
}
