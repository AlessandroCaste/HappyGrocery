package com.code.dima.happygrocery;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.example.alessandro.barcodeyeah.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.List;


public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PieChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
        List<Integer> valueList = ProductListDetails.getInstance().getProductPerCategory();
        List<String> labels = ProductListDetails.getInstance().getCategoriesNames();
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
        Product lastProduct = ProductListDetails.getInstance().getLastProduct();
        String name = lastProduct.getName();
        String price = lastProduct.getPrice();
        int imageID = lastProduct.getImageID();
        ImageView imageView = findViewById(R.id.dashboardProdImage);
        TextView nameText = findViewById(R.id.dashboardProdName);
        TextView priceText = findViewById(R.id.dashboardProdDetails);
        nameText.setText(name);
        priceText.setText(price);
        imageView.setImageResource(imageID);
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
}
