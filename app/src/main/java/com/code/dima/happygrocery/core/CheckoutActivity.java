package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.adapter.ProductCheckoutAdapter;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.model.GroceryDetails;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;
import com.code.dima.happygrocery.tasks.EndGroceryTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

            //Updating Firebase database
            DatabaseAdapter db = DatabaseAdapter.openInWriteMode(getApplicationContext());
            GroceryDetails gd = db.getLastGrocery();
            if(gd != null && gd.getAmount() != 0) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uId = currentUser.getUid();
                final DatabaseReference myRef = database.getReference("users/" + uId + "/groceries");
                myRef.push().setValue(gd);
                Toast.makeText(getApplicationContext(), "Grocery successfuly registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error in registration!",Toast.LENGTH_SHORT).show();
            }
            db.close();

            ShoppingCart.getInstance().clearShoppingCart();
            Intent i = new Intent();
            setResult(Activity.RESULT_OK,i);
            finish();
        }




}

