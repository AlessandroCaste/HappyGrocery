package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.database.InsertProductInDBTask;
import com.code.dima.happygrocery.database.UpdateProductQuantityInDBTask;
import com.code.dima.happygrocery.exception.NoSuchProductException;
import com.code.dima.happygrocery.model.Category;
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
    Product currentProduct;
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

        previousActivityName = getIntent().getStringExtra("activityName");
        barcode = getIntent().getStringExtra("barcode");

        findViewById(R.id.loadingPanel).bringToFront();

        if (previousActivityName.equals("DashboardActivity")) {
            cardView.setVisibility(View.INVISIBLE);
            quantityButton.setVisibility(View.INVISIBLE);
            cancel.hide();
            accept.hide();

            jsonParse(this);
        }
        // Instantiate the RequestQueue.


    }

    private void jsonParse(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="https://my-json-server.typicode.com/AlessandroCaste/HappyGroceryDB/products?barcode="+barcode;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    // Info extraction
                    JSONArray jsonArray = response;
                    JSONObject reader = jsonArray.getJSONObject(0);
                    Category category = Category.valueOf(reader.getString("category"));
                    String name       = reader.getString("name");
                    float price       = Float.parseFloat(reader.getString("price"));
                    String barcode    = reader.getString("barcode");
                    float weight      = Float.parseFloat(reader.getString("weight"));
                    currentProduct    = new Product(category,name,price,barcode,weight,1,0);

                    //Visibility settings
                    cardView.setVisibility(View.VISIBLE);
                    quantityButton.setVisibility(View.VISIBLE);
                    cancel.show();
                    accept.show();
                    findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

                    //Items customization
                    TextView nameView = findViewById(R.id.info_name);
                    TextView priceView = findViewById(R.id.info_price);
                    //TextView producer = findViewById(R.id.info_producer);
                    TextView weightView = findViewById(R.id.info_weight);
                    nameView.setText(currentProduct.getName());
                    priceView.setText(String.valueOf(currentProduct.getPrice()));
                    weightView.setText(Float.toString(currentProduct.getWeight()) + "g");

                } catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                    e.printStackTrace();
                    builder.setTitle("Barcode not recognized!")
                            .setMessage("Barcode not found!\nAsk shopping assistants!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(R.drawable.ic_error_white_dp)
                            .show();
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Error occured", error);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                builder.setTitle("Server Problem!")
                        .setMessage("Service unavailable\nTry again later!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(R.drawable.ic_error_white_dp)
                        .show();
                finish();
            }
        });
        queue.add(request);
    }


    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }


    public void cancel(View view){
        finish();
        overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
        // this.onBackPressed();
    }

    public void acceptProduct (View view) {

        if(previousActivityName.equals("DashboardActivity")) {
            // this call will automatically update lastProduct
            int quantity = Integer.parseInt(quantityButton.getNumber());
            currentProduct.setQuantity(quantity);
            ShoppingCart.getInstance().addProduct(currentProduct);
            //update in database
            InsertProductInDBTask task = new InsertProductInDBTask(getApplicationContext(), currentProduct);
            task.execute();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        if(previousActivityName.equals("ShoppingCartActivity")) {
            //int recyclerPosition = getIntent().getIntExtra("position",1);
            Intent returnIntent = new Intent();
            currentProduct = ShoppingCart.getInstance().getProductWithBarcode(barcode);
            returnIntent.putExtra("category", currentProduct.getCategory().name());
            int newQuantity = Integer.parseInt(quantityButton.getNumber());
            try {
                ShoppingCart.getInstance().updateQuantity(currentProduct, newQuantity);
            } catch (NoSuchProductException e) {
                e.printStackTrace();
            }
            // update in database
            UpdateProductQuantityInDBTask task = new UpdateProductQuantityInDBTask(getApplicationContext(), currentProduct, newQuantity);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }

    }

}