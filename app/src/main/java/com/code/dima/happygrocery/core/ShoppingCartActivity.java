package com.code.dima.happygrocery.core;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import com.code.dima.happygrocery.exception.NoSuchCategoryException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.adapter.ProductAdapter;
import com.code.dima.happygrocery.model.ProductList;
import com.example.alessandro.barcodeyeah.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ShoppingCartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProductList productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        productList = ProductList.getInstance();
        try {
            initializeRecycler();
        } catch (NoSuchCategoryException e) {
            e.printStackTrace();
        }
        initializeButtons();
        createDummyData();

    }

    private void initializeRecycler() throws NoSuchCategoryException {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, productList.getProductsInCategory("FOOD"));
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
    }

    private void initializeButtons() {
        CircleImageView food =findViewById(R.id.food);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
    //   .setAction("Action", null).show()

    private void createDummyData() {
        for(int i= 0; i < 2; i++)
        productList.addProduct(new Product(0));
        for(int i= 0; i < 2; i++)
            productList.addProduct(new Product(1));
        for(int i= 0; i < 2; i++)
            productList.addProduct(new Product(2));

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.main_menu, menu);
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
                if(query.length() > 15) { Toast.makeText(ShoppingCartActivity.this, getString(R.string.long_search), Toast.LENGTH_SHORT).show(); }
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onClick(View v)  {
        Vibrator myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        myVib.vibrate(50);
        switch(v.getId()) {
            case (R.id.food):
                setTitle("Food");
                try {
                    adapter.set(productList.getProductsInCategory("FOOD"));
                    updateAnimation(recyclerView);
                } catch (NoSuchCategoryException e) {
                    e.printStackTrace();
                }
                break;
            case (R.id.beverage):
                setTitle("Beverage");
                try {
                    adapter.set(productList.getProductsInCategory("BEVERAGE"));
                    updateAnimation(recyclerView);
                } catch (NoSuchCategoryException e) {
                    e.printStackTrace();
                }
                break;
            case (R.id.kids):
                setTitle("Kids and stationery");
                try {
                    adapter.set(productList.getProductsInCategory("KIDS"));
                    updateAnimation(recyclerView);
                } catch (NoSuchCategoryException e) {
                    e.printStackTrace();
                }
                break;
            case (R.id.home):
                setTitle("Home needs");
                try {
                    adapter.set(productList.getProductsInCategory("HOME"));
                    updateAnimation(recyclerView);
                } catch (NoSuchCategoryException e) {
                    e.printStackTrace();
                }
                break;
            case (R.id.clothing):
                setTitle("Clothing");
                try {
                    adapter.set(productList.getProductsInCategory("CLOTHING"));
                    updateAnimation(recyclerView);
                } catch (NoSuchCategoryException e) {
                    e.printStackTrace();
                }
                break;
            case (R.id.others):
                setTitle("Other Categories");
                try {
                    adapter.set(productList.getProductsInCategory("OTHER"));
                    updateAnimation(recyclerView);
                } catch (NoSuchCategoryException e) {
                    e.printStackTrace();
                }
                break;
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

}



