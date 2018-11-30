package com.code.dima.happygrocery.core;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.code.dima.happygrocery.model.Product;
import com.example.alessandro.barcodeyeah.R;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_page);
        extractData();
    //    query();
    }



    /**Copre la back arrow dell'action bar**/
    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }


    // Classe test per rendere configurabile la gestione dei prodotti. Poi verrò naturalmente allargata/rimossa
    private void extractData() {
        Product fragola = new Product();
        TextView nome = findViewById(R.id.info_name);
        TextView price = findViewById(R.id.info_price);
        TextView weight = findViewById(R.id.info_weight);
        ElegantNumberButton quantityButton = findViewById(R.id.quantityButton);
        CircleImageView productType = findViewById(R.id.productType);
        nome.setText(fragola.getName());
        price.setText(String.valueOf(fragola.getPrice()));
        weight.setText(Float.toString(fragola.getWeight()) + "g");
        quantityButton.setRange(1,10);
        quantityButton.getNumber();
        int id = getResources().getIdentifier("com.code.dima.happygrocery:drawable/" + "food", null, null);
        productType.setImageResource(id);
    }

    public void cancel(View view){
        finish();
       // this.onBackPressed();
    }

    private void query(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.myjson.com/bins/avbua")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    //HappyGroceryRESTInterface restInterface =
         //   retrofit.create(HappyGroceryRESTInterface.class);


}

