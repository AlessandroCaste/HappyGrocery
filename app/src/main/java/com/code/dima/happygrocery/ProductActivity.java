package com.code.dima.happygrocery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
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

public class ProductActivity extends AppCompatActivity {

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_page);
        extractData();
        //initEAN();
    }



    /**Copre la back arrow dell'action bar**/
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

    // Classe test per rendere confiugurabile la gestione dei prodotti. Poi verrò naturalmente allargata/rimossa
    private void extractData() {
        Product fragola = new Product();
        TextView nome = findViewById(R.id.info_name);
        TextView price = findViewById(R.id.info_price);
        TextView description = findViewById(R.id.info_description);
        String tipology;
        nome.setText(fragola.getName());
        price.setText(fragola.getPrice());
        description.setText(fragola.getDescription());
    }

    public void cancel(View view){
        finish();
        overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
       // this.onBackPressed();
    }

}

