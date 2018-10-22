package com.code.dima.happygrocery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.alessandro.barcodeyeah.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnTakePicture, btnScanBarcode;
    TextView ciao;
    int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        btnScanBarcode.setOnClickListener(this);
        ciao = findViewById(R.id.ciao);
    }



    @Override
    public void onClick(View v) {
        Intent i = new Intent(this,LivePreviewActivity.class);
        startActivity(i);
        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
            ciao.setText(data.getExtras().get("code").toString());
    }

}
