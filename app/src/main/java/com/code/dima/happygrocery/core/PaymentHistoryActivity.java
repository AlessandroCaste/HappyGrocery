package com.code.dima.happygrocery.core;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.adapter.GroceryAdapter;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.model.GroceryDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<GroceryDetails> groceries;
    GroceryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DatabaseAdapter db = DatabaseAdapter.openInWriteMode(getApplicationContext());
        groceries = db.queryGroceries();
        db.close();

        final List<GroceryDetails> imported = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("groceries");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    collectEverything((HashMap<String,String>)dataSnapshot.getValue());
                }
                Toast.makeText(getApplicationContext(),"Database updated",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase Database error", "Failed to read value.", error.toException());
            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_history_app_bar);

        Toolbar toolbar = findViewById(R.id.payment_history_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.paymentHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroceryAdapter(this, imported);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

    }

    private void collectEverything(HashMap<String,String> users) {
        HashMap<String,String> ciao = new HashMap<>();

        System.out.println(users.toString());
    }

}