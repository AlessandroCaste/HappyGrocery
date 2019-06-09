package com.code.dima.happygrocery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.wear.widget.CircularProgressLayout;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.utils.Category;
import com.code.dima.happygrocery.utils.ImageRetriever;

public class ProductDetailsActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        TextView name = findViewById(R.id.product_details_name_text);
        TextView quantity = findViewById(R.id.product_details_quantity_text);
        TextView price = findViewById(R.id.product_details_price_text);
        ImageView image = findViewById(R.id.product_details_image);

        Intent intent = getIntent();
        String nameString = intent.getStringExtra("name");
        name.setText(nameString);
        String quantityString = "quantity: " + intent.getStringExtra("quantity");
        quantity.setText(quantityString);
        String priceString = "price: " + intent.getStringExtra("price");
        priceString += getString(R.string.currency);
        price.setText(priceString);
        Category category = Category.valueOf(intent.getStringExtra("category"));

        ImageRetriever retriever = ImageRetriever.getInstance(this);
        int imageID = retriever.retrieveImageID(nameString, category);
        image.setImageResource(imageID);

        setAmbientEnabled();
    }

    public void onClick(View view) {
        finish();
    }
}
