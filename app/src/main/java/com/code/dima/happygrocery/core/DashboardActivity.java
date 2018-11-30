package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ProductList;
import com.code.dima.happygrocery.model.Category;
import com.example.alessandro.barcodeyeah.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PieChart chart;

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

        if (chart == null) {
            chart = findViewById(R.id.DashboardPieChart);
            chart.setDrawHoleEnabled(true);
            chart.setHoleColor(Color.WHITE);
            chart.setHoleRadius(75);
            chart.setCenterText(getString(R.string.currency) + "30");
            chart.setCenterTextSize(36);
            Description desc = new Description();
            desc.setText("");
            chart.setDescription(desc);
            chart.setDrawEntryLabels(false);
            Legend legend = chart.getLegend();
            legend.setEnabled(false);
        }
        setChartData();
        updateLastProduct();
    }

    private void setChartData() {

        ArrayList<PieEntry> yValues = new ArrayList<>();
        List<Integer> valueList = ProductList.getInstance().getNumberOfProductsPerCategory();
        List<String> labels = ProductList.getInstance().getCategoryNames();
        ArrayList<Integer> colors = new ArrayList<>();
        for(int i = 0; i < labels.size(); i ++) {
            colors.add(Category.getCategoryColor(labels.get(i)));
        }

        PieEntry newEntry;
        for (int i = 0; i < valueList.size(); i++) {
            newEntry = new PieEntry(valueList.get(i), i);
            newEntry.setLabel(labels.get(i));
            //yValues.add(new PieEntry(valueList.get(i), labels.get(i)));
            yValues.add(newEntry);
        }


        PieDataSet dataset = new PieDataSet(yValues, "Categories");
        dataset.setSliceSpace(3);
        dataset.setSelectionShift(3);
        dataset.setColors(colors);
        dataset.setDrawValues(false);
        PieData data = new PieData(dataset);
        //data.setValueTextSize(11f);
        //data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.invalidate();
    }

    private void updateLastProduct () {
        String name;
        String price;
        int imageID;
        try {
            Product lastProduct = ProductList.getInstance().getLastProduct();
            name = lastProduct.getName();
            price = String.valueOf(lastProduct.getPrice()) + getResources().getString(R.string.currency);
            imageID = lastProduct.getImageID();
        } catch (NoLastProductException e) {
            name = getResources().getString(R.string.dashboard_default_prod_name);
            price = getResources().getString(R.string.dashboard_default_prod_details);
            imageID = R.drawable.fragole;
        }
        ImageView imageView = findViewById(R.id.dashboardProdImage);
        TextView nameText = findViewById(R.id.dashboardProdName);
        TextView priceText = findViewById(R.id.dashboardProdDetails);
        nameText.setText(name);
        priceText.setText(price);
        imageView.setImageResource(imageID);
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
                Intent i = new Intent(this,ProductActivity.class);
                i.putExtra("barcode",contents);
                i.putExtra("activityName","DashboardActivity");
                startActivityForResult(i, getResources().getInteger(R.integer.PRODUCT_REQUEST_CODE));
                overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
            }

        } else if (resultCode == getResources().getInteger(R.integer.PRODUCT_REQUEST_CODE)) {
            Bundle bundle   = data.getExtras();
            String category = bundle.getString("category");
            String name     = bundle.getString("name");
            float price     = bundle.getFloat("price");
            String barcode  = bundle.getString("barcode");
            float weight    = bundle.getFloat("weight");
            int quantity    = bundle.getInt("quantity");
            int imageID     = bundle.getInt("imageId");

            Product lastProduct = new Product(Category.valueOf(category), name, price, barcode, weight, quantity, imageID);
            String debug = new String();
            overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
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
}
