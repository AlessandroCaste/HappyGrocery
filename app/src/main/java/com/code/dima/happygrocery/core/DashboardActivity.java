package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.Category;
import com.code.dima.happygrocery.model.ShoppingCart;
import com.code.dima.happygrocery.tasks.ClearGroceryTask;
import com.code.dima.happygrocery.tasks.InitializeImageRetrieverTask;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;

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

    String url = "";
    String shopName = "";

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

        if(url.isEmpty() || shopName.isEmpty()) {
            url = getIntent().getStringExtra("url");
            shopName = getIntent().getStringExtra("name");
            toolbar.setTitle(shopName);
        }
        getSupportActionBar().setTitle(shopName);

        updateChartData();

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

        updateLastProduct();

        // initialize image retriever
        new InitializeImageRetrieverTask(DashboardActivity.this).execute();
    }

    private void updateChartData() {
        new UpdateChartDataTask().execute();
    }


    private void updateLastProduct () {
        //new UpdateLastProductTask(DashboardActivity.this).execute();
        try {
            Product lastProduct = ShoppingCart.getInstance().getLastProduct();
            String name = lastProduct.getName();
            String price = lastProduct.getPrice() + getResources().getString(R.string.currency);
            int imageID = lastProduct.getImageID();
            findViewById(R.id.dashboard_empty_card_layout).setVisibility(View.INVISIBLE);
            ImageView imageView = findViewById(R.id.dashboard_last_product_image);
            TextView nameText = findViewById(R.id.dashboard_last_product_name);
            TextView priceText = findViewById(R.id.dashboard_last_product_price);
            nameText.setText(name);
            priceText.setText(price);
            imageView.setImageResource(imageID);
            findViewById(R.id.dashboard_full_card_layout).setVisibility(View.VISIBLE);
        } catch (NoLastProductException e) {
            View emptyCard = findViewById(R.id.dashboard_empty_card_layout);
            View fullCard = findViewById(R.id.dashboard_full_card_layout);
            emptyCard.setVisibility(View.VISIBLE);
            fullCard.setVisibility(View.INVISIBLE);
        }
    }

    public void addNewProduct(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
        integrator.setBeepEnabled(true);
        integrator.setPrompt(getResources().getString(R.string.prompt));
        integrator.initiateScan();
        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
    }

    public void openCheckout(View view) {
        Intent i = new Intent(this, CheckoutActivity.class);
        startActivityForResult(i,getResources().getInteger(R.integer.CHECKOUT_CODE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                //String formatName = data.getStringExtra("SCAN_RESULT_FORMAT");
                Intent i = new Intent(this, ProductActivity.class);
                i.putExtra("queryUrl", url);
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
        } else if (requestCode == getResources().getInteger(R.integer.CHECKOUT_CODE)) {
            if(resultCode == Activity.RESULT_OK) {
                finish();
                overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
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
            AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
            alert.setTitle(R.string.clear_grocery_title);
            alert.setMessage(R.string.clear_grocery_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ShoppingCart.getInstance().clearShoppingCart();
                    ClearGroceryTask task = new ClearGroceryTask(DashboardActivity.this);
                    task.execute();
                    finish();
                    overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
                }
            });
            alert.setNegativeButton(R.string.CANCEL,null);
            alert.setCancelable(false);
            alert.show();
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
            AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
            alert.setTitle(R.string.clear_grocery_title);
            alert.setMessage(R.string.clear_grocery_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ShoppingCart.getInstance().clearShoppingCart();
                    ClearGroceryTask task = new ClearGroceryTask(DashboardActivity.this);
                    task.execute();
                    finish();
                    overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
                }
            });
            alert.setNegativeButton(R.string.CANCEL,null);
            alert.setCancelable(false);
            alert.show();
        } else if (id == R.id.payment_history) {
            Intent i = new Intent(this, PaymentHistoryActivity.class);
            startActivity(i);
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
        } else if(id == R.id.end_grocery) {
            Intent i = new Intent(this, CheckoutActivity.class);
            startActivityForResult(i,getResources().getInteger(R.integer.CHECKOUT_CODE));
        } else if (id == R.id.log_out) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.log_out_title);
            alert.setMessage(R.string.log_out_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    ShoppingCart.getInstance().clearShoppingCart();
                    ClearGroceryTask task = new ClearGroceryTask(DashboardActivity.this);
                    task.execute();
                    mAuth.signOut();
                    finish();
                    overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
                }
            });
            alert.setNegativeButton(R.string.CANCEL,null);
            alert.setCancelable(false);
            alert.show();
        } else if (id == R.id.about_us) {
            AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
            alert.setTitle(R.string.about_us_title);
            alert.setMessage(R.string.about_us_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.setCancelable(false);
            alert.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
