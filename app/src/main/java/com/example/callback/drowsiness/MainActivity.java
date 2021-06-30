package com.example.callback.drowsiness;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDex;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import static com.example.callback.drowsiness.Settings.readFromFile;

public class MainActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener {
    FrameLayout frame;
    public static boolean isFirstTime;
    public static int countofexceeds;
    public static boolean EnteredPN=false;

    //Array for asking all the permissions
    private String[] PERMISSIONS={
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_PHONE_NUMBERS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            android.Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        frame =(FrameLayout) findViewById(R.id.frame);
        countofexceeds=1;
        Window window=this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.tfe_color_accent));
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new monitor_menu()).commit();
        Toast.makeText(getApplicationContext(),"Swipe left for menu",Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        isFirstTime = MyPreferences.isFirst(MainActivity.this);
        if(isFirstTime)
        {
            Intent help = new Intent(MainActivity.this, help.class);
            startActivity(help);
        }

        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_dialog_alert)
                    .setTitle("Closing Application")
                    .setMessage("Are you sure you want to close this application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,new monitor_menu()).commit();
        }
        else if(id == R.id.help_page)
        {
            Intent hp = new Intent(MainActivity.this,help.class);
            startActivity(hp);

        }
        else if(id == R.id.additional_details)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,new AdditionalDetails()).commit();

        }
      else if (id == R.id.nav_send) {

            getSupportFragmentManager().beginTransaction().replace(R.id.frame,new contactus()).commit();
        }
      else if(id == R.id.settings_page){
            Intent hp = new Intent(MainActivity.this,Settings.class);
            startActivity(hp);
        }
      else if(id==R.id.longtermdisease){
          Intent hp=new Intent(MainActivity.this,LongTermDisease.class);
          startActivity(hp);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
