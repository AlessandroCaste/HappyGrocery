package com.code.dima.happygrocery;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.alessandro.barcodeyeah.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class Main2Activity extends AppCompatActivity {

    public static final String KEY_ANIM_TYPE = "anim_type";
    public static final String KEY_TITLE = "anim_title";

    public enum TransitionType {
        ExplodeJava, ExplodeXML,SlideJava,SlideXML,FadeJava,FadeXML
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String text="978020137962";// Whatever you need to encode in the QR code
        ImageView imageView = findViewById(R.id.ye);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.EAN_13,800,350);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void explodeTransitionByCode(View view){
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);

        Intent i = new Intent(this,TransitionActivity.class);
        startActivity(i,options.toBundle());

    }

}
