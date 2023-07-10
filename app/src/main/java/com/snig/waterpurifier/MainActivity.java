package com.snig.waterpurifier;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    UserInfo userInfo;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    DatabaseReference reference;
    NotificationCompat.Builder builder;
    WaterInfo waterInfo;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;
    String fromEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this);

        waterInfo = new WaterInfo();

        reference = FirebaseDatabase.getInstance("https://water-purifier-1c9cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("water");

        userInfo = new Gson().fromJson(getIntent().getExtras().getString("userInfo"), UserInfo.class);
        fromEdit = getIntent().getExtras().getString("fromEdit");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

//        builder = new NotificationCompat.Builder(MainActivity.this, "notification");
//        builder.setContentTitle("Check your filter system!");
//        builder.setContentText("We have detected an abnormal level between intake and outtake water from the device. It seems that" +
//                "the filtration system is not working properly");
//        builder.setSmallIcon(R.drawable.baseline_circle_notifications_24);
//        builder.setAutoCancel(true);
//
//        NotificationManager manager = getSystemService(NotificationManager.class);
//        manager.notify(1, builder.build());
//
        toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);
//
//        long date = Math.floorDiv(System.currentTimeMillis(),86400000L);
//        graphCalculation(date);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView navName = (TextView) headerView.findViewById(R.id.nav_name);
        navName.setText(userInfo.name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText(userInfo.email);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_home) {
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.nav_home).setChecked(true);
                    openFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.bottom_graph) {
                    clearNavigationSelection(navigationView);
//                    openFragment(new NotAvailableFragment());
                    openFragment(new GraphFragment());
                    return true;
                } else if (itemId == R.id.bottom_reminder) {
                    clearNavigationSelection(navigationView);
                    openFragment(new RemindFragment());
                    return true;
                } else if (itemId == R.id.bottom_search) {
                    clearNavigationSelection(navigationView);
                    openFragment(new NotAvailableFragment());
//                    openFragment(new SearchFragment());
                    return true;
                }
                return false;
            }
        });


        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            openFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
            openFragment(new HomeFragment());
        } else if (itemId == R.id.nav_settings) {
            openFragment(new NotAvailableFragment());
//            openFragment(new SettingsFragment());
        } else if (itemId == R.id.nav_editprofile) {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            intent.putExtra("userInfo", new Gson().toJson(userInfo));
            startActivity(intent);
        } else if (itemId == R.id.nav_about) {
            openFragment(new AboutFragment());
        } else if (itemId == R.id.nav_share) {
            ShareApp(MainActivity.this);
        } else if (itemId == R.id.nav_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.commit();
    }

    private void clearNavigationSelection(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_home).setChecked(false);
        menu.findItem(R.id.nav_about).setChecked(false);
        menu.findItem(R.id.nav_settings).setChecked(false);
        menu.findItem(R.id.nav_editprofile).setChecked(false);
    }

    private void ShareApp(Context context){
        // code here
        final String appPakageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Now: https://play.google.com/store/apps/details?id=" + appPakageName );
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }


}

