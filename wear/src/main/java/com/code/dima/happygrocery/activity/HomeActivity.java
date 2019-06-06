package com.code.dima.happygrocery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import androidx.wear.ambient.AmbientModeSupport;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.utils.HomeMessageReceiver;
import com.google.android.gms.wearable.Wearable;


public class HomeActivity extends WearableActivity
        implements AmbientModeSupport.AmbientCallbackProvider {

    private HomeMessageReceiver receiver;
    private TextView text;

    private final int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        text = findViewById(R.id.home_activity_text);

        text.setText(R.string.waiting);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new HomeMessageReceiver(this);
        Wearable.getMessageClient(this).addListener(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Wearable.getMessageClient(this).removeListener(receiver);
        receiver = null;
    }

    public void startDashboard(String nodeID) {
        Intent startDashboard = new Intent(this, DashboardActivity.class);
        startDashboard.putExtra("phoneID", nodeID);
        startActivity(startDashboard);
        startActivityForResult(startDashboard, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                text.setText(R.string.grocery_closed);
            } else if (resultCode == RESULT_CANCELED) {
                text.setText(R.string.connection_lost);
            }
        }
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
    }

}
