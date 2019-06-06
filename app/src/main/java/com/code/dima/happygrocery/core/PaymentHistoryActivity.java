package com.code.dima.happygrocery.core;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.code.dima.happygrocery.model.ShoppingCart;
import com.code.dima.happygrocery.tasks.ClearGroceryTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class PaymentHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<GroceryDetails> groceries;
    GroceryAdapter adapter;
    ArrayList<GroceryDetails> taskDesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_history_app_bar);
        findViewById(R.id.loadingPanelActivity).setVisibility(View.VISIBLE);
        findViewById(R.id.deleteFirebaseHistory).setVisibility(View.INVISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();
        final DatabaseReference myRef = database.getReference("users/"+ uId+"/groceries");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<GroceryDetails> genericTypeIndicator = new GenericTypeIndicator<GroceryDetails>() {};
                for(DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String userkey = dsp.getKey();
                    GroceryDetails miao = dataSnapshot.child(userkey).getValue(genericTypeIndicator);
                    taskDesList.add(miao);
                }
                findViewById(R.id.loadingPanelActivity).setVisibility(View.INVISIBLE);
                findViewById(R.id.deleteFirebaseHistory).setVisibility(View.VISIBLE);

                Toolbar toolbar = findViewById(R.id.payment_history_toolbar);
                setSupportActionBar(toolbar);
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                // Data has been checked on remote DB
                recyclerView = findViewById(R.id.paymentHistoryRecyclerView);

                // Setupping the layout and the recycler view
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new GroceryAdapter(getApplicationContext(), taskDesList);
                DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
                decoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
                recyclerView.addItemDecoration(decoration);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase Database error", "Failed to read value.", error.toException());
            }
        });
    }

    public void eraseHistory(View v){

        AlertDialog.Builder alert = new AlertDialog.Builder(PaymentHistoryActivity.this);
        alert.setTitle(R.string.clear_firebase_title);
        alert.setMessage(R.string.clear_firebase_message);
        alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Erasing Firebase database
                DatabaseAdapter db = DatabaseAdapter.openInWriteMode(getApplicationContext());
                GroceryDetails gd = db.getLastGrocery();
                if(gd != null) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uId = currentUser.getUid();
                    final DatabaseReference myRef = database.getReference("users/" + uId);
                    myRef.setValue("/groceries");
                    Toast.makeText(getApplicationContext(), "History has been successfully erased", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error accessing online database!",Toast.LENGTH_SHORT).show();
                }
                db.close();
                finish();
            }
        });
        alert.setNegativeButton(R.string.CANCEL,null);
        alert.setCancelable(false);
        alert.show();
    }

}