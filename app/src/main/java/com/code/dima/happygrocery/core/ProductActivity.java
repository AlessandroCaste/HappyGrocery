package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;
import com.example.alessandro.barcodeyeah.R;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProductActivity extends AppCompatActivity {

    private String code;
    String previousActivityName;
    Product lastProduct;
    ElegantNumberButton quantityButton;
    CardView cardView;
    FloatingActionButton cancel;
    FloatingActionButton accept;
    String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_page);

        cardView = findViewById(R.id.card_view);
        quantityButton = findViewById(R.id.quantityButton);
        cancel = findViewById(R.id.cancelButton);
        accept = findViewById(R.id.acceptButton);


        barcode = getIntent().getStringExtra("barcode");
        previousActivityName = getIntent().getStringExtra("activityName");
        findViewById(R.id.loadingPanel).bringToFront();

        //only for testing
        extractData();
        jsonParse();

        // Instantiate the RequestQueue.


    }

    private void jsonParse() {
        RequestQueue queue = Volley.newRequestQueue(this);

        final TextView mTextView = findViewById(R.id.textView2);

        String url ="https://my-json-server.typicode.com/AlessandroCaste/HappyGroceryDB/db";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = response.getJSONArray("products");
                    for(int i = 0; i<5; i++) {
                        JSONObject products = jsonArray.getJSONObject(i);
                        String category = products.getString("category");
                        String barcode = products.getString("barcode");
                        String name = products.getString("name");
                        String price = products.getString("price");
                        mTextView.setText(name);
                        cardView.setVisibility(View.VISIBLE);
                        quantityButton.setVisibility(View.VISIBLE);
                        cancel.show();
                        accept.show();
                        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Error occured", error);
                finish();
            }
        });
        queue.add(request);

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
            // this call will automatically update lastProduct
            ShoppingCart.getInstance().addProduct(lastProduct);
            InsertInDatabaseTask task = new InsertInDatabaseTask(getApplicationContext());
            task.execute(lastProduct);
            Intent returnIntent = new Intent();
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


    private class InsertInDatabaseTask extends AsyncTask<Product, Void, Void> {

        private Context context;

        public InsertInDatabaseTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Product... products) {
            if (products.length == 1) {
                Product product = products[0];
                DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context);
                adapter.insertProductIntoProductList(product);
                adapter.close();
            }
            return null;
        }
    }

}