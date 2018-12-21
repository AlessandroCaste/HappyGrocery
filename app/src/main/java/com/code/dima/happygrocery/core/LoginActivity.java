package com.code.dima.happygrocery.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.tasks.AddGroceryInDBTask;
import com.code.dima.happygrocery.tasks.RestoreActiveGroceryTask;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PieChart chart;
    private List<String> labels;
    private List<Integer> colors;
    FirebaseUser user;
    Context context;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private final static int RC_SIGN_IN = 9001;


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

        context = this;

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    getSupportActionBar().setTitle(getResources().getString(R.string.HappyGrocery));
                    boolean restoreNeeded = checkActiveGrocery();
                    if (restoreNeeded) {
                        RestoreActiveGroceryTask task = new RestoreActiveGroceryTask(getApplicationContext());
                        task.execute();
                        Intent callDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(callDashboard);
                    }
                } else {

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

    }

    /*
    Checks whether there's an active grocery in the db to be restored
     */
    private boolean checkActiveGrocery() {
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(getApplicationContext());
        boolean answer = adapter.queryActiveGrocery();
        adapter.close();
        return answer;
    }

    public void onButtonClick(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
        integrator.setBeepEnabled(true);
        integrator.setPrompt(getResources().getString(R.string.qr_prompt));
        integrator.initiateScan();
        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if ( result != null) {
            if (((IntentResult) result).getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(this, DashboardActivity.class);
                startActivity(i);
               overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
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
    //    Picasso.get().load(user.getPhotoUrl()).into(profilePicture);
         //profileName.setText(user.getDisplayName());
         //profileMail.setText(user.getEmail());
    }

    @Override
    protected void onResume() {
        super.onResume();
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



    public void onDrawerButtonClick (MenuItem menuItem){
        int id = menuItem.getItemId();

        if  (id == R.id.log_out) {

            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
            alert.setTitle(R.string.log_out_title);
            alert.setMessage(R.string.log_out_message);
            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                }
            });
            alert.setNegativeButton(R.string.CANCEL,null);
            alert.setCancelable(false);
            alert.show();
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void newShoppingClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getResources().getString(R.string.prompt));
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        AddGroceryInDBTask task = new AddGroceryInDBTask(getApplicationContext());
        task.execute();
    }

}
