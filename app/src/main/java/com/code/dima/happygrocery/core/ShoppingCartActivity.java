package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.adapter.ProductAdapter;
import com.code.dima.happygrocery.model.ShoppingCart;


import de.hdodenhof.circleimageview.CircleImageView;


public class ShoppingCartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_app_bar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        initializeRecycler();
        initializeButtons();

    }

    private void initializeRecycler() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String category;
        try {
            category = ShoppingCart.getInstance().getLastProduct().getCategory().name();
            categoryToTitle(category);
        } catch (NoLastProductException e) {
            category = "FOOD";
            categoryToTitle(category);
        }
        adapter = new ProductAdapter(this, ShoppingCart.getInstance().getProductsInCategory(category));
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
    }

    private void initializeButtons() {
        CircleImageView food = findViewById(R.id.food);
        CircleImageView beverage = findViewById(R.id.beverage);
        CircleImageView kids = findViewById(R.id.kids);
        CircleImageView home = findViewById(R.id.home);
        CircleImageView clothing = findViewById(R.id.clothing);
        CircleImageView others = findViewById(R.id.others);
        food.setOnClickListener(this);
        beverage.setOnClickListener(this);
        kids.setOnClickListener(this);
        home.setOnClickListener(this);
        clothing.setOnClickListener(this);
        others.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                if (query.length() > 15) {
                    Toast.makeText(ShoppingCartActivity.this, getString(R.string.long_search), Toast.LENGTH_SHORT).show();
                }
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }


    public void categoryToTitle(String name) {
        switch(name){
            case "FOOD":     setTitle(getString(R.string.Food_Title));
                break;
            case "BEVERAGE": setTitle(getString(R.string.Beverage_Title));
                break;
            case "KIDS":     setTitle(getString(R.string.Kids_Title));
                break;
            case "HOME":     setTitle(getString(R.string.Home_Title));
                break;
            case "CLOTHING": setTitle(getString(R.string.Clothing_Title));
                break;
            case "OTHER":    setTitle(getString(R.string.Other_Title));
                break;
            default:         setTitle(getString(R.string.Food_Title));
        }
    }

    @Override
    public void onClick(View v) {
        Vibrator myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        myVib.vibrate(50);
        switch (v.getId()) {
            case (R.id.food):
                setTitle(getString(R.string.Food_Title));
                adapter.set(ShoppingCart.getInstance().getProductsInCategory("FOOD"));
                updateAnimation(recyclerView);
                break;
            case (R.id.beverage):
                setTitle(getString(R.string.Beverage_Title));
                adapter.set(ShoppingCart.getInstance().getProductsInCategory("BEVERAGE"));
                updateAnimation(recyclerView);
                break;
            case (R.id.kids):
                setTitle(getString(R.string.Kids_Title));
                adapter.set(ShoppingCart.getInstance().getProductsInCategory("KIDS"));
                updateAnimation(recyclerView);
                break;
            case (R.id.home):
                setTitle(getString(R.string.Home_Title));
                adapter.set(ShoppingCart.getInstance().getProductsInCategory("HOME"));
                updateAnimation(recyclerView);
                break;
            case (R.id.clothing):
                setTitle(getString(R.string.Clothing_Title));
                adapter.set(ShoppingCart.getInstance().getProductsInCategory("CLOTHING"));
                updateAnimation(recyclerView);
                break;
            case (R.id.others):
                setTitle(getString(R.string.Other_Title));
                adapter.set(ShoppingCart.getInstance().getProductsInCategory("OTHER"));
                updateAnimation(recyclerView);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == getResources().getInteger(R.integer.CART_REQUEST_CODE)) {

            if (resultCode == Activity.RESULT_OK) {
                String categoryName = data.getStringExtra("category");
                adapter.set(ShoppingCart.getInstance().getProductsInCategory(categoryName));
            }
        }
    }


    private void updateAnimation(final RecyclerView recyclerView) {
        adapter.notifyDataSetChanged();
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}



