package com.code.dima.happygrocery.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.code.dima.happygrocery.wearable.DataPaths;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.tasks.AddGroceryInDBTask;
import com.code.dima.happygrocery.tasks.RestoreActiveGroceryTask;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean authFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(!authFlag) {
                        authFlag = true;
                        getSupportActionBar().setTitle(getResources().getString(R.string.HappyGrocery));
                    }
                } else {
                    authFlag = false;
                     AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                            .Builder(R.layout.login)
                            .setPhoneButtonId(R.id.phoneButton)
                            //.setTosAndPrivacyPolicyId(R.id.baz)
                            .build();

                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(true)
                                    .setAuthMethodPickerLayout(customLayout)
                                    .setAlwaysShowSignInMethodScreen(true)
                                    .setTheme(R.style.CustomTheme)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .build(),
                            9001);
                }
            }
        };

        // at start, checks if there's an active grocery to be restored
        boolean restoreNeeded = checkActiveGrocery();
        if (restoreNeeded) {
            RestoreActiveGroceryTask task = new RestoreActiveGroceryTask(LoginActivity.this);
            task.execute();
            Intent callDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String url = sharedPref.getString(getString(R.string.shop_url), "");
            String name = sharedPref.getString(getString(R.string.shop_name), "");
            callDashboard.putExtra("name",name);
            callDashboard.putExtra("url",url);
            startActivity(callDashboard);
        }

        // notify the watch that this node is able to support it
        Wearable.getCapabilityClient(this).addLocalCapability(DataPaths.WATCH_SERVER);
    }

    /*
    Checks whether there's an active grocery in the db to be restored
     */
    private boolean checkActiveGrocery() {
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(LoginActivity.this);
        boolean answer = adapter.queryActiveGrocery();
        adapter.close();
        return answer;
    }

//    public void onButtonClick(View view){
//        IntentIntegrator integrator = new IntentIntegrator(this);
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
//        integrator.setBeepEnabled(true);
//        integrator.setPrompt(getResources().getString(R.string.qr_prompt));
//        integrator.initiateScan();
//        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK && data != null) {
                String url = data.getStringExtra("SCAN_RESULT");
                Intent i = new Intent(this, DashboardActivity.class);
                parseAndLaunch(url, i);
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void parseAndLaunch(String url, final Intent i) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    // Info extraction
                    JSONArray jsonArray = response;
                    JSONObject reader = jsonArray.getJSONObject(0);
                    String name = reader.getString("name");
                    String url = reader.getString("url");
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.shop_url), url);
                    editor.putString(getString(R.string.shop_name), name);
                    editor.apply();
                    i.putExtra("name",name);
                    i.putExtra("url",url);
                    // insert a new entry in the db for the grocery
                    AddGroceryInDBTask task = new AddGroceryInDBTask(LoginActivity.this, name);
                    task.execute();
                    startActivity(i);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                } catch (JSONException e) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setTitle(R.string.error_qr_store_title);
                    alert.setMessage(R.string.error_qr_store_message);
                    alert.setPositiveButton(R.string.OK, null);
                    alert.setCancelable(false);
                    alert.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Error occured", error);
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setTitle(R.string.error_connecting_title);
                alert.setMessage(R.string.error_connecting_message);
                alert.setPositiveButton(R.string.OK, null);
                alert.setCancelable(false);
                alert.show();
            }
        });
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enable for debugging purpose
        // if(!BuildConfig.DEBUG)
            mAuth.addAuthStateListener(mAuthStateListener);
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
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.clear_grocery) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.clear_grocery_login_title);
            alert.setMessage(R.string.clear_grocery_login_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            alert.setCancelable(false);
            alert.show();
        } else if (id == R.id.payment_history) {
            Intent i = new Intent(LoginActivity.this, PaymentHistoryActivity.class);
            startActivity(i);
            overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
        } else if (id == R.id.end_grocery) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.clear_grocery_login_title);
            alert.setMessage(R.string.clear_grocery_login_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            alert.setCancelable(false);
            alert.show();
        } else if (id == R.id.log_out) {
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
            alert.setTitle(R.string.log_out_title);
            alert.setMessage(R.string.log_out_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
                }
            });
            alert.setNegativeButton(R.string.CANCEL,null);
            alert.setCancelable(false);
            alert.show();
        } else if (id == R.id.about_us) {
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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


    @Override
    protected void onPause(){
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void newShoppingClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getResources().getString(R.string.qr_prompt));
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
    }

}
