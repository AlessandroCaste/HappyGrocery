package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.Category;
import com.code.dima.happygrocery.model.ShoppingCart;
import com.example.alessandro.barcodeyeah.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PieChart chart;
    private List<String> labels;
    private List<Integer> colors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // workaround to add a new active grocery to database
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(getApplicationContext());
        Cursor c = adapter.querySQL("SELECT * FROM grocery_history WHERE active = 1");
        if (c.getCount() == 0) {
            adapter.insertNewGrocery("11/07/2018", "Esselungone");
        }
        adapter.close();

        labels = ShoppingCart.getInstance().getCategoryNames();

        colors = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            colors.add(Category.getCategoryColor(labels.get(i)));
        }

        chart = findViewById(R.id.DashboardPieChart);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(75);
        String centerText = getResources().getString(R.string.currency) + "0.00";
        chart.setCenterText(centerText);
        chart.setCenterTextSize(36);
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setDrawEntryLabels(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        updateChartData();
        updateLastProduct();
    }

    private void updateChartData() {
        ShoppingCart cart = ShoppingCart.getInstance();
        List<Integer> valueList = cart.getNumberOfProductsPerCategory();

        ArrayList<PieEntry> yValues = new ArrayList<>();
        PieEntry newEntry;
        for (int i = 0; i < valueList.size(); i++) {
            newEntry = new PieEntry(valueList.get(i), i);
            newEntry.setLabel(labels.get(i));
            yValues.add(newEntry);
        }

        PieDataSet dataset = new PieDataSet(yValues, "Categories");
        dataset.setSliceSpace(3);
        dataset.setSelectionShift(4);
        dataset.setColors(colors);
        dataset.setDrawValues(false);
        PieData data = new PieData(dataset);
        chart.setData(data);
        chart.invalidate();
    }


    private void updateLastProduct () {
        CardView card = findViewById(R.id.dashboardCardView);
        try {
            Product lastProduct = ShoppingCart.getInstance().getLastProduct();
            String name = lastProduct.getName();
            String price = lastProduct.getPrice() + getResources().getString(R.string.currency);
            int imageID = lastProduct.getImageID();
            card.setActivated(true);
            ImageView imageView = findViewById(R.id.dashboardProdImage);
            TextView nameText = findViewById(R.id.dashboardProdName);
            TextView priceText = findViewById(R.id.dashboardProdDetails);
            nameText.setText(name);
            priceText.setText(price);
            imageView.setImageResource(imageID);
        } catch (NoLastProductException e) {
            card.setActivated(false);
        }

    }


    public void onButtonClick(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
        integrator.setBeepEnabled(true);
        integrator.setPrompt(getResources().getString(R.string.prompt));
        integrator.initiateScan();
        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String formatName = data.getStringExtra("SCAN_RESULT_FORMAT");
                Intent i = new Intent(this, ProductActivity.class);
                i.putExtra("barcode", contents);
                i.putExtra("activityName", "DashboardActivity");
                startActivityForResult(i, getResources().getInteger(R.integer.PRODUCT_REQUEST_CODE));
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        }

         if (requestCode == getResources().getInteger(R.integer.PRODUCT_REQUEST_CODE)) {

            if(resultCode == Activity.RESULT_OK) {
                updateChartData();
                Bundle bundle = data.getExtras();
                //String category = bundle.getString("category");
                //String name = bundle.getString("name");
                //float price = bundle.getFloat("price");
                //String barcode = bundle.getString("barcode");
                //float weight = bundle.getFloat("weight");
                //int quantity = bundle.getInt("quantity");
                //int imageID = bundle.getInt("imageId");
                updateLastProduct();
                overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            }
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shopping_cart) {
            Intent i = new Intent(this,ShoppingCartActivity.class);
            startActivity(i);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class QueryShoppingCart extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean updateNeeded = false;
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(getApplicationContext());
            List<Product> productList = adapter.queryProductList();
            if (productList.size() > 0) {
                updateNeeded = true;
                ShoppingCart cart = ShoppingCart.getInstance();
                for (Product product : productList) {
                    cart.addProduct(product);
                }
            }
            adapter.close();
            return updateNeeded;
        }

        protected void OnPostExecute (Boolean update) {
            // executed on UI thread
            // set new data and chart.invalidate()
            if (update) {
                updateChartData();
            }
        }

    }

}
