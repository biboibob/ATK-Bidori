package com.example.atkmobile;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.regex.PatternSyntaxException;

public class Warehouse extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    CircularProgressBar bar;

    TextView textPercent,textAlert;
    ImageView imgAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        /*Memunculkan Action Bar*/
        Toolbar toolbar = findViewById(R.id.toolbarWarehouse);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        /*Drawer*/
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navViewWarehouse);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        /*Navigation Drawer Menu*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*Take Layout*/
        bar = findViewById(R.id.circularProgressBar);
        textPercent = findViewById(R.id.textPercent);
        textAlert = findViewById(R.id.textPercentAlert);
        imgAlert = findViewById(R.id.imageAlert);


        new backgroundProccess().execute();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.dailyincome_2:
                Intent intentDailyIncome  = new Intent(this,DailyIncome_2.class);
                startActivity(intentDailyIncome);
                break;
            case R.id.navhome:
                Intent intent  = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.warehouse:
                Intent intentWarehouse  = new Intent(this,Warehouse.class);
                startActivity(intentWarehouse);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }


    class backgroundProccess extends AsyncTask<Void, Void, Void> {

        Float resultPercent;
        DecimalFormat df = new DecimalFormat("#.##");

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select count(*) as total, ((select count(*) from gudang where stock < 1)/count(*))*100 as percentItem from gudang");

                while (rs.next()) {
                    resultPercent = 100 - rs.getFloat(2);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            DecimalFormat df = new DecimalFormat("##");

            bar.setProgressWithAnimation(resultPercent, (long) 1000);
            textPercent.setText(df.format(resultPercent) + "%");

            int red = Color.parseColor("#EF6363");
            int yellow = Color.parseColor("#ECF16B");
            int green = Color.parseColor("#71CE86");

            if(resultPercent <= 25) {
                bar.setProgressBarColor(red);
                textPercent.setTextColor(red);

                textAlert.setText("Your should maintanance your storage!");
                textAlert.setTextColor(red);

                imgAlert.setBackgroundResource(R.drawable.icon_warning_red);
            }if (resultPercent < 50 && resultPercent > 25){
                bar.setProgressBarColor(yellow);
                textPercent.setTextColor(yellow);

                textAlert.setText("Careful, maintanance your storage.");
                textAlert.setTextColor(yellow);

                imgAlert.setBackgroundResource(R.drawable.icon_warning_yellow);
            }if(resultPercent >=50) {
                bar.setProgressBarColor(green);
                textPercent.setTextColor(green);

                textAlert.setText("Your storage in good condition");
                textAlert.setTextColor(green);

                imgAlert.setBackgroundResource(R.drawable.icon_info_green);

            }
            super.onPostExecute(aVoid);
        }
    }

    public void lihatMostWanted(View view) {
        Intent intent = new Intent(this, Mostwanted.class);
        startActivity(intent);
    }

    public void lihatStorage(View view) {
        Intent intent = new Intent(this, Storage.class);
        startActivity(intent);
    }

    public void lihatOneYearSale(View view) {
        Intent intent = new Intent(this, oneYearSale.class);
        startActivity(intent);
    }
}