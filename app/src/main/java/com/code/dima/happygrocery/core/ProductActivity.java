package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.code.dima.happygrocery.model.Product;
import com.example.alessandro.barcodeyeah.R;

public class ProductActivity extends AppCompatActivity {

    private String code;
    String previousActivityName;
    Product lastProduct;
    ElegantNumberButton quantityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_page);
        quantityButton = findViewById(R.id.quantityButton);
        String ciao = getIntent().getStringExtra("barcode");
        previousActivityName = getIntent().getStringExtra("activityName");
        extractData();
    }



    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }


    // Classe test per rendere configurabile la gestione dei prodotti. Poi verrò naturalmente allargata/rimossa
    private void extractData() {
        Product fragola = new Product();
        TextView nome = findViewById(R.id.info_name);
        TextView price = findViewById(R.id.info_price);
        TextView producer = findViewById(R.id.info_producer);
        TextView weight = findViewById(R.id.info_weight);
        nome.setText(fragola.getName());
        price.setText(String.valueOf(fragola.getPrice()));
        weight.setText(Float.toString(fragola.getWeight()) + "g");
        lastProduct = fragola;
    }

    public void cancel(View view){
        finish();
        overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
        // this.onBackPressed();
    }

    public void acceptProduct (View view) {

        if(previousActivityName.equals("DashboardActivity")) {
            Intent returnIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("name", lastProduct.getName());
            bundle.putString("category",lastProduct.getCategory().name());
            bundle.putFloat("price",lastProduct.getPrice());
            bundle.putString("barcode", getIntent().getStringExtra("barcode"));
            bundle.putFloat("weight",lastProduct.getWeight());
            bundle.putInt("quantity", lastProduct.getQuantity());
            bundle.putInt("imageId", lastProduct.getImageID());
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        if(previousActivityName.equals("ShoppingCartActivity")) {
            int recyclerPosition = getIntent().getIntExtra("position",1);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("quantity",quantityButton.getNumber());
            returnIntent.putExtra("position",recyclerPosition);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }

    }

}