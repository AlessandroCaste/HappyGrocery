package com.code.dima.happygrocery.core;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.adapter.GroceryAdapter;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.model.GroceryDetails;

import java.util.List;


public class PaymentHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<GroceryDetails> groceries;
    GroceryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        queryDatabase();
        adapter = new GroceryAdapter(this, groceries);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
    }


    private void queryDatabase () {
        DatabaseAdapter db = DatabaseAdapter.openInWriteMode(getApplicationContext());
        groceries = db.queryGroceries();
        db.close();
    }
}
