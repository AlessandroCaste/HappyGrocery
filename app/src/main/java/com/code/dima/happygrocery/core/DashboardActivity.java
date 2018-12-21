package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.code.dima.happygrocery.tasks.AddGroceryInDBTask;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PieChart chart;
    private List<String> labels;
    private List<Integer> colors;
    FirebaseUser user;
    Context context;


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

        chart = findViewById(R.id.DashboardPieChart);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(75);
        chart.setCenterTextSize(36);
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setDrawEntryLabels(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        chart.setTouchEnabled(true);
        CustomMarkerView marker = new CustomMarkerView(this, R.layout.marker_layout);
        chart.setMarker(marker);
        chart.invalidate();

        context = this;

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
    protected void onStart() {
        super.onStart();
        NavigationView nav_header = findViewById(R.id.nav_view);
        TextView profileName = nav_header.getHeaderView(0).findViewById(R.id.profileName);
        TextView profileMail = nav_header.getHeaderView(0).findViewById(R.id.profileMail);
        CircleImageView profilePicture = nav_header.getHeaderView(0).findViewById(R.id.profilePicture);

        user = FirebaseAuth.getInstance().getCurrentUser();
        //Picasso.get().load(user.getPhotoUrl()).into(profilePicture);
       // profileName.setText(user.getDisplayName());
       // profileMail.setText(user.getEmail());
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

        if (id == R.id.clear_grocery) {

        } else if (id == R.id.payment_history) {
            Intent i = new Intent(context, PaymentHistoryActivity.class);
            startActivity(i);
            overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
        } else if (id == R.id.payment_methods) {

        } else if (id == R.id.log_out) {

        } else if (id == R.id.about_us) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onDrawerButtonClick (MenuItem menuItem){
        int id = menuItem.getItemId();

        if  (id == R.id.log_out) {

            AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
            alert.setTitle(R.string.log_out_title);
            alert.setMessage(R.string.log_out_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    finish();
                    overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
                }
            });
            alert.setNegativeButton(R.string.CANCEL,null);
            alert.setCancelable(false);
            alert.show();

        } else if (id == R.id.payment_history) {
//            Intent i = new Intent(context, PaymentHistoryActivity.class);
//            startActivity(i);
//            overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
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
            Product lastProduct;
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
            if (lastProductExists) {
                findViewById(R.id.dashboard_empty_card_layout).setVisibility(View.INVISIBLE);
                findViewById(R.id.dashboard_full_card_layout).setVisibility(View.VISIBLE);
                ImageView imageView = findViewById(R.id.dashboardProdImage);
                TextView nameText = findViewById(R.id.dashboardProdName);
                TextView priceText = findViewById(R.id.dashboardProdDetails);
                nameText.setText(name);
                priceText.setText(price);
                imageView.setImageResource(imageID);
            } else {
                findViewById(R.id.dashboard_empty_card_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.dashboard_full_card_layout).setVisibility(View.INVISIBLE);
            }
        }
    }
}
