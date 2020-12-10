package com.example.atkmobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Class.forName;

public class Storage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    TextView totalItem, totalDiscount, totalZeroItem, lastUpdated;
    GridLayout gridLayout;

    TextView ATKText,PRLText,OLRText,PRMText,ULTText,MNNText,ACCText,ARTText;
    ImageView ATKImg,ARTImg,PRLImg,OLRImg,ACCImg,PRMImg,ULTImg,MNNImg;

    TextView titleCondition, descCondition;
    ImageView imgCondition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

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

        /*Header Information*/
        totalItem = (TextView) findViewById(R.id.totalItemRecored);
        totalDiscount = (TextView)findViewById(R.id.totalDiscount);
        totalZeroItem = (TextView)findViewById(R.id.totalZeroItem);
        lastUpdated = (TextView)findViewById(R.id.lastUpdated);

        /*Get child from Grid View*/
        gridLayout = findViewById(R.id.gridContent);

        ATKText = findViewById(R.id.conditionATK);
        PRLText = findViewById(R.id.conditionPeralatan);
        OLRText = findViewById(R.id.conditionOLR);
        PRMText = findViewById(R.id.conditionPRM);
        ULTText = findViewById(R.id.conditionULT);
        MNNText = findViewById(R.id.conditionMNN);
        ACCText = findViewById(R.id.conditionACC);
        ARTText = findViewById(R.id.conditionART);

        ATKImg = findViewById(R.id.imgATK);
        PRLImg = findViewById(R.id.imgPeralatan);
        OLRImg = findViewById(R.id.imgOLR);
        PRMImg = findViewById(R.id.imgPRM);
        ULTImg = findViewById(R.id.imgULT);
        MNNImg = findViewById(R.id.imgMNN);
        ACCImg = findViewById(R.id.imgACC);
        ARTImg = findViewById(R.id.imgART);

        //Acumulation Condition
        titleCondition = findViewById(R.id.titleCondition);
        descCondition = findViewById(R.id.descCondition);
        imgCondition = findViewById(R.id.imgCondition);
        
        new storageEx().execute();
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

    class storageEx extends AsyncTask<Void, Void, Void> {

        String resultTotalItem, resultTotalDiscount,resultTotalZero,resultDate;

        Float kondisiATK;
        Float kondisiPeralatan;
        Float kondisiOlahraga;
        Float kondisiPramuka;
        Float kondisiUlangTaun;
        Float kondisiMainan;
        Float kondisiAksesoris;
        Float kondisiART;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(barang) as total_item, (SELECT COUNT(diskon) FROM gudang WHERE diskon > 0) as total_diskon, (SELECT COUNT(stock) from gudang WHERE stock < 1)as total_stock, (SELECT MAX(tgl_update_akhir) from gudang)as last_updated from gudang");

                while(rs.next()) {
                    resultTotalItem = rs.getString(1);
                    resultTotalDiscount = rs.getString(2);
                    resultTotalZero = rs.getString(3);

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String stringDate = formatter.format(rs.getDate(4));

                    Date date = formatter.parse(stringDate);
                    PrettyTime PT = new PrettyTime();

                    resultDate = PT.format(date);
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select ((select count(*) from gudang WHERE id_barang LIKE 'ATK%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'ATK%'))*100 as ATK, " +
                        "((select count(*) from gudang WHERE id_barang LIKE 'PRL%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'PRL%'))*100 as peralatan, " +
                        "((select count(*) from gudang WHERE id_barang LIKE 'ORL%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'ORL%'))*100 as olahraga, " +
                        "((select count(*) from gudang WHERE id_barang LIKE 'PRM%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'PRM%'))*100 as pramuka, " +
                        "((select count(*) from gudang WHERE id_barang LIKE 'ULT%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'ULT%'))*100 as ulangtaun, " +
                        "((select count(*) from gudang WHERE id_barang LIKE 'MNN%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'MNN%'))*100 as mainan, " +
                        "((select count(*) from gudang WHERE id_barang LIKE 'AKS%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'AKS%'))*100 as aksesoris, " +
                        "((select count(*) from gudang WHERE id_barang LIKE 'ART%' AND stock > 0)/ (SELECT count(*) from gudang WHERE id_barang LIKE 'ART%'))*100 as pramuka");

                while(rs.next()) {

                    kondisiATK = rs.getFloat(1);
                    kondisiPeralatan = rs.getFloat(2);
                    kondisiOlahraga = rs.getFloat(3);
                    kondisiPramuka = rs.getFloat(4);
                    kondisiUlangTaun = rs.getFloat(5);
                    kondisiMainan = rs.getFloat(6);
                    kondisiAksesoris = rs.getFloat(7);
                    kondisiART = rs.getFloat(8);

                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            totalItem.setText(resultTotalItem);
            totalDiscount.setText(resultTotalDiscount);
            totalZeroItem.setText(resultTotalZero);
            lastUpdated.setText(resultDate);

            int totalCondition = 0;

            if(kondisiATK >= 50) {
                ATKText.setText("Good Condition");
                ATKText.setTextColor(Color.parseColor("#71CE86"));

                ATKImg.setColorFilter(Color.parseColor("#71CE86"));

                totalCondition++;

            } else {
                ATKText.setText("Bad Condition");
                ATKText.setTextColor(Color.parseColor("#EF6363"));

                ATKImg.setColorFilter(Color.parseColor("#EF6363"));

                totalCondition++;
            }

            if(kondisiPeralatan >= 50) {
                PRLText.setText("Good Condition");
                PRLText.setTextColor(Color.parseColor("#71CE86"));

                PRLImg.setColorFilter(Color.parseColor("#71CE86"));

                totalCondition++;

            } else {
                PRLText.setText("Bad Condition");
                PRLText.setTextColor(Color.parseColor("#EF6363"));

                PRLImg.setColorFilter(Color.parseColor("#EF6363"));
            }

            if(kondisiOlahraga >= 50) {
                OLRText.setText("Good Condition");
                OLRText.setTextColor(Color.parseColor("#71CE86"));

                OLRImg.setColorFilter(Color.parseColor("#71CE86"));
                totalCondition++;

            } else {
                OLRText.setText("Bad Condition");
                OLRText.setTextColor(Color.parseColor("#EF6363"));

                OLRImg.setColorFilter(Color.parseColor("#EF6363"));
                totalCondition++;
            }

            if(kondisiPramuka >= 50) {
                PRMText.setText("Good Condition");
                PRMText.setTextColor(Color.parseColor("#71CE86"));

                PRMImg.setColorFilter(Color.parseColor("#71CE86"));
                totalCondition++;

            } else {
                PRMText.setText("Bad Condition");
                PRMText.setTextColor(Color.parseColor("#EF6363"));

                PRMImg.setColorFilter(Color.parseColor("#EF6363"));
            }

            if(kondisiUlangTaun >= 50) {
                ULTText.setText("Good Condition");
                ULTText.setTextColor(Color.parseColor("#71CE86"));

                ULTImg.setColorFilter(Color.parseColor("#71CE86"));
                totalCondition++;

            } else {
                ULTText.setText("Bad Condition");
                ULTText.setTextColor(Color.parseColor("#EF6363"));

                ULTImg.setColorFilter(Color.parseColor("#EF6363"));
            }

            if(kondisiMainan >= 50) {
                MNNText.setText("Good Condition");
                MNNText.setTextColor(Color.parseColor("#71CE86"));

                MNNImg.setColorFilter(Color.parseColor("#71CE86"));
                totalCondition++;

            } else {
                MNNText.setText("Bad Condition");
                MNNText.setTextColor(Color.parseColor("#EF6363"));

                MNNImg.setColorFilter(Color.parseColor("#EF6363"));
            }

            if(kondisiAksesoris >= 50) {
                ACCText.setText("Good Condition");
                ACCText.setTextColor(Color.parseColor("#71CE86"));

                ACCImg.setColorFilter(Color.parseColor("#71CE86"));
                totalCondition++;

            } else {
                ACCText.setText("Bad Condition");
                ACCText.setTextColor(Color.parseColor("#EF6363"));

                ACCImg.setColorFilter(Color.parseColor("#EF6363"));
            }

            if(kondisiART >= 50) {
                ARTText.setText("Good Condition");
                ARTText.setTextColor(Color.parseColor("#71CE86"));

                ARTImg.setColorFilter(Color.parseColor("#71CE86"));
                totalCondition++;

            } else {
                ARTText.setText("Bad Condition");
                ARTText.setTextColor(Color.parseColor("#EF6363"));

                ARTImg.setColorFilter(Color.parseColor("#EF6363"));
            }


            /*Condition Check*/
            if(totalCondition >= 4) {

                imgCondition.setImageResource(R.drawable.icon_mood);

                titleCondition.setText("Good Condition");
                titleCondition.setTextColor(Color.parseColor("#71CE86"));

                descCondition.setText("You have " + totalCondition +"/8 of good condition");
            } else {

                imgCondition.setImageResource(R.drawable.icon_badmood);

                descCondition.setText("Bad Condition");
                titleCondition.setTextColor(Color.parseColor("#EF6363"));

                descCondition.setText("You have " + totalCondition +"/8 of bad condition");
            }



          

           /* final int childCount = gridLayout.getChildCount();

            for (int i = 0; i < childCount; i++) {
                ViewGroup v = (ViewGroup) gridLayout.getChildAt(i);
                int LayoutChildsCount = v.getChildCount();

                for (int j = 0; j < LayoutChildsCount; j++ ) {
                    ViewGroup vg = (ViewGroup) v.getChildAt(j);
                    int Layout2ChildsCount = vg.getChildCount();

                    for (int k = 0; k < Layout2ChildsCount; k++ ) {
                        ViewGroup vh = (ViewGroup) vg.getChildAt(k);
                        int Layout3ChildsCount = vg.getChildCount();

                        for (int l = 0; l < Layout3ChildsCount; l++ ) {
                            View v1 = vh.getChildAt(l);

                            *//*((TextView)v1).setText("Good Condition");
                            ((TextView)v1).setTextColor(Color.parseColor("#71CE86"));*//*

                        }
                    }

                }
            }*/

            super.onPostExecute(aVoid);
        }

    }

    public void startIntent(View view) {
        switch (view.getId()) {
            case R.id.alatTulisKantor:

                Intent intentATK = new Intent(this, DetailStorage.class);
                intentATK.putExtra("SESSION", "ATK");
                intentATK.putExtra("SESSION_TITLE", "Alat Tulis Kantor");
                startActivity(intentATK);

                break;

            case R.id.peralatan:

                Intent intentPRL = new Intent(this, DetailStorage.class);
                intentPRL.putExtra("SESSION", "PRL");
                intentPRL.putExtra("SESSION_TITLE", "Peralatan");
                startActivity(intentPRL);

                break;

            case R.id.olahraga:

                Intent intentORL = new Intent(this, DetailStorage.class);
                intentORL.putExtra("SESSION", "ORL");
                intentORL.putExtra("SESSION_TITLE", "Olahraga");
                startActivity(intentORL);

                break;

            case R.id.pramuka:

                Intent intentPRM = new Intent(this, DetailStorage.class);
                intentPRM.putExtra("SESSION", "PRM");
                intentPRM.putExtra("SESSION_TITLE", "Pramuka");
                startActivity(intentPRM);

                break;

            case R.id.ulangTahun:

                Intent intentUTH = new Intent(this, DetailStorage.class);
                intentUTH.putExtra("SESSION", "ULT");
                intentUTH.putExtra("SESSION_TITLE", "Ulang Tahun");
                startActivity(intentUTH);

                break;

            case R.id.mainan:

                Intent intentMNN = new Intent(this, DetailStorage.class);
                intentMNN.putExtra("SESSION", "MNN");
                intentMNN.putExtra("SESSION_TITLE", "Mainan");
                startActivity(intentMNN);

                break;

            case R.id.aksesoris:

                Intent intentAKS = new Intent(this, DetailStorage.class);
                intentAKS.putExtra("SESSION", "AKS");
                intentAKS.putExtra("SESSION_TITLE", "Aksesoris");
                startActivity(intentAKS);

                break;

            case R.id.alatRumahTangga:

                Intent intentART = new Intent(this, DetailStorage.class);
                intentART.putExtra("SESSION", "ART");
                intentART.putExtra("SESSION_TITLE", "Alat Rumah Tangga");
                startActivity(intentART);

                break;
        }
    }

}