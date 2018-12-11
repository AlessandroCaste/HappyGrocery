package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
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

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.Category;
import com.code.dima.happygrocery.model.ShoppingCart;
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

        updateChartData();
        updateLastProduct();

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
        AddGroceryInDatabase task = new AddGroceryInDatabase(getApplicationContext());
        task.execute();

//        labels = ShoppingCart.getInstance().getCategoryNames();
//
//        colors = new ArrayList<>();
//        for (int i = 0; i < labels.size(); i++) {
//            colors.add(Category.getCategoryColor(labels.get(i)));
//        }

        chart = findViewById(R.id.DashboardPieChart);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(75);
        //String centerText = getResources().getString(R.string.currency) + "0.00";
        //chart.setCenterText(centerText);
        chart.setCenterTextSize(36);
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setDrawEntryLabels(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        chart.invalidate();
    }

    private void updateChartData() {
        new UpdateChartDataTask().execute();
    }


    private void updateLastProduct () {
        new UpdateLastProductTask().execute();
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
        } else if (requestCode == getResources().getInteger(R.integer.PRODUCT_REQUEST_CODE)) {
            if(resultCode == Activity.RESULT_OK) {
                //updateChartData();
                //updateLastProduct();
                overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateChartData();
        updateLastProduct();
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


    private class AddGroceryInDatabase extends AsyncTask<Void, Void, Void> {

        private Context context;

        public AddGroceryInDatabase(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // workaround to add a new active grocery to database
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context);
            Cursor c = adapter.querySQL("SELECT * FROM grocery_history WHERE active = 1");
            if (c.getCount() == 0) {
                adapter.insertNewGrocery("11/07/2018", "Esselungone");
            }
            adapter.close();
            return null;
        }
    }

    private class UpdateChartDataTask extends AsyncTask<Void, Void, Void> {

        private PieData data;
        private String amountString;

        @Override
        protected Void doInBackground(Void... voids) {
            ShoppingCart cart = ShoppingCart.getInstance();

            // the first time the dashboard is created, sets labels and colors
            if (labels == null) {
                labels = cart.getCategoryNames();
                colors = new ArrayList<>();
                for (int i = 0; i < labels.size(); i++) {
                    colors.add(Category.getCategoryColor(labels.get(i)));
                }
            }

            List<Integer> valueList = cart.getNumberOfProductsPerCategory();
            float amount = cart.getAmount();
            amountString = getResources().getString(R.string.currency) + String.format("%.2f", amount);

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
            data = new PieData(dataset);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            chart.setData(data);
            chart.setCenterText(amountString);
            chart.invalidate();
        }
    }

    private class UpdateLastProductTask extends AsyncTask<Void, Void, Boolean> {

        private String name;
        private String price;
        private int imageID;

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean lastProductExists = true;
            Product lastProduct = null;
            try {
                lastProduct = ShoppingCart.getInstance().getLastProduct();
                name = lastProduct.getName();
                price = lastProduct.getPrice() + getResources().getString(R.string.currency);
                imageID = lastProduct.getImageID();
            } catch (NoLastProductException e) {
                lastProductExists = false;
            }
            return lastProductExists;
        }

        @Override
        protected void onPostExecute(Boolean lastProductExists) {
            CardView card = findViewById(R.id.dashboardCardView);
            if (lastProductExists) {
                card.setVisibility(View.VISIBLE);
                ImageView imageView = findViewById(R.id.dashboardProdImage);
                TextView nameText = findViewById(R.id.dashboardProdName);
                TextView priceText = findViewById(R.id.dashboardProdDetails);
                nameText.setText(name);
                priceText.setText(price);
                imageView.setImageResource(imageID);
            } else {
                card.setVisibility(View.INVISIBLE);
            }
        }
    }
}
