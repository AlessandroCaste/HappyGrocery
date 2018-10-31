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

public class ProductActivity extends AppCompatActivity {

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_page);
        //initEAN();
        initAnimation();
    }


    /**Configura l'aspetto della pagina
    private void initPage() {
        code = getIntent().getStringExtra("barcode");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("yipee")
    }*/

    /**Copre lo spostamento in alto nella gerarchia d'attività dall'action bar**/
    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }

    /**Garantisce l'explode animation all'avvio dell'activity**/
    private void initAnimation() {

        Explode enterTransition = new Explode();
        enterTransition.setDuration(getResources().getInteger(R.integer.explode_animation_duration));
        getWindow().setEnterTransition(enterTransition);

    }

    /**Utilizza la libreria zxing per ricostruire il barcode a partire dal codice
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
     */
}

