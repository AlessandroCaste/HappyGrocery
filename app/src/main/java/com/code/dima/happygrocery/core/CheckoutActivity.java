package com.code.dima.happygrocery.core;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.adapter.ProductCheckoutAdapter;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;
import com.code.dima.happygrocery.tasks.EndGroceryTask;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductCheckoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_checkout);


        recyclerView = findViewById(R.id.recyclerCheckout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Product> cart = ShoppingCart.getInstance().cloneShoppingCart();
        adapter = new ProductCheckoutAdapter(this, cart);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        }

        public void cancelCheckout(View view) {
            onBackPressed();
        }

        public void acceptCheckout(View view) {
            EndGroceryTask task = new EndGroceryTask(this);
            task.execute();
            ShoppingCart.getInstance().clearShoppingCart();
            Intent i = new Intent();
            setResult(Activity.RESULT_OK,i);
            finish();
        }




}

