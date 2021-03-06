package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.code.dima.happygrocery.wearable.WearableUpdateTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.database.InsertProductInDBTask;
import com.code.dima.happygrocery.database.UpdateProductQuantityInDBTask;
import com.code.dima.happygrocery.exception.NoSuchProductException;
import com.code.dima.happygrocery.model.Category;
import com.code.dima.happygrocery.model.ImageRetriever;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProductActivity extends AppCompatActivity {

    String previousActivityName;
    Product currentProduct;
    ElegantNumberButton quantityButton;
    CardView cardView;
    FloatingActionButton cancel;
    FloatingActionButton accept;
    String url;
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


        findViewById(R.id.loadingPanel).bringToFront();

        if (previousActivityName.equals("DashboardActivity")) {
            cardView.setVisibility(View.INVISIBLE);
            quantityButton.setVisibility(View.INVISIBLE);
            cancel.hide();
            accept.hide();
            url = getIntent().getStringExtra("queryUrl");
            barcode = getIntent().getStringExtra("barcode");
            jsonParse(this);
        } else {
            barcode = getIntent().getStringExtra("barcode");
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            restoreSC(ShoppingCart.getInstance().getProductWithBarcode(barcode));
        }


    }

    private void jsonParse(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String urlQuery =url+barcode;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlQuery, null, new Response.Listener<JSONArray>() {
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
                    float discount    = Float.parseFloat(reader.getString("discount"));
                    price = price - price*discount;
                    int imageID = ImageRetriever.getInstance(ProductActivity.this).retrieveImageID(name, category);
                    currentProduct    = new Product(category,name,price,barcode,weight,1,imageID);

                    //Visibility settings
                    cardView.setVisibility(View.VISIBLE);
                    quantityButton.setVisibility(View.VISIBLE);
                    cancel.show();
                    accept.show();
                    findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

                    restoreSC(currentProduct);

                } catch (JSONException e) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                    alert.setTitle(R.string.error_barcode_title);
                    alert.setMessage(R.string.error_barcode_message);
                    alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Error occured", error);
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                alert.setTitle(R.string.error_connecting_title);
                alert.setMessage(R.string.error_connecting_message);
                alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alert.setCancelable(false);
                alert.show();
            }
        });
        queue.add(request);
    }


    private void restoreSC(Product product) {
        String name = product.getName();
        float price = product.getPrice();
        float weight = product.getWeight();
        int quantity = product.getQuantity();
        int imageID = product.getImageID();
        Category category = product.getCategory();
        int categoryID = ImageRetriever.getInstance(ProductActivity.this).retrieveImageID("", category);
        //Items customization
        TextView nameView = findViewById(R.id.info_name);
        TextView priceView = findViewById(R.id.info_price);
        quantityButton.setNumber(Integer.toString(quantity));
        TextView weightView = findViewById(R.id.info_weight);
        ImageView imageView = findViewById(R.id.product_activity_image_view);
        CircleImageView categoryView = findViewById(R.id.product_cicle_image_view);
        nameView.setText(name);
        String priceText = price + getResources().getString(R.string.currency);
        String weightText = weight + "g";
        priceView.setText(priceText);
        weightView.setText(weightText);
        imageView.setImageResource(imageID);
        categoryView.setImageResource(categoryID);
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
            new UpdateProductQuantityInDBTask(getApplicationContext(), currentProduct, newQuantity).execute();
            // update the wearable's data
            new WearableUpdateTask(this, false).execute();
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }

    }

}