package com.code.dima.happygrocery.core;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.code.dima.happygrocery.model.Product;
import com.example.alessandro.barcodeyeah.R;

public class ProductActivity extends AppCompatActivity {

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_page);
        extractData();
    }



    /**Copre la back arrow dell'action bar**/
    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }


    // Classe test per rendere configurabile la gestione dei prodotti. Poi verrò naturalmente allargata/rimossa
    private void extractData() {
        Product fragola = new Product(0);
        TextView nome = findViewById(R.id.info_name);
        TextView price = findViewById(R.id.info_price);
        TextView producer = findViewById(R.id.info_producer);
        TextView weight = findViewById(R.id.info_weight);
        nome.setText(fragola.getName());
        price.setText(String.valueOf(fragola.getPrice()));
        weight.setText(Float.toString(fragola.getWeight()) + "g");

    }

    public void cancel(View view){
        finish();
        overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
       // this.onBackPressed();
    }

}

