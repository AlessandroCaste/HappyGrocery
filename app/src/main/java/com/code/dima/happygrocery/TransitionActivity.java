package com.code.dima.happygrocery;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alessandro.barcodeyeah.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

public class TransitionActivity extends AppCompatActivity {

    private String code;

    public enum TransitionType {
        ExplodeJava, ExplodeXML, SlideJava, SlideXML, FadeJava, FadeXML
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_activity);
        initPage();
        initEAN();
        initAnimation();
    }

    private void initPage() {
        TextView text = findViewById(R.id.textView2);
        text.setText(getIntent().getStringExtra("barcode"));
        code = getIntent().getStringExtra("barcode");
        Button btnExit = (Button) findViewById(R.id.exit_button);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("yipee");
    }


    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }

    private void initAnimation() {

        Explode enterTransition = new Explode();
        enterTransition.setDuration(getResources().getInteger(R.integer.explode_animation_duration));
        getWindow().setEnterTransition(enterTransition);

    }

    private void initEAN() {
        ImageView imageView = findViewById(R.id.ye);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.EAN_13,600,250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}

