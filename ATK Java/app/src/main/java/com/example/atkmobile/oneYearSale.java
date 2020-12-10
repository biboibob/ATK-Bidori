package com.example.atkmobile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.MathUtils;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anychart.core.annotations.Line;
import com.anychart.scales.Linear;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class oneYearSale extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private ArrayList itemArrayList = new ArrayList<String>();  //List items Array

    TextView ValueATK,ValuePRL,ValueORL,ValuePRM,ValueULT,ValueMNN,ValueACC,ValueART,ValueTotal;

    LinearLayout image;

    private MyAppAdapter myAppAdapter; //Array Adapter
    private RecyclerView recyclerView; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    SearchView search;

    String query;

    progressDialogGIF progressDialogGIF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_year_sale);


        /*Memunculkan Action Bar*/
        Toolbar toolbar = findViewById(R.id.toolbarOneYearSale);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        /*Drawer*/
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navViewOneYearSale);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        /*Navigation Drawer Menu*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*RecycleView*/
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewOYS);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(mLayoutManager);

        itemArrayList =  new ArrayList(); //arraylist barang

        /*Set Value*/
        ValueATK = findViewById(R.id.textView7);
        ValuePRL = findViewById(R.id.textView8);
        ValueORL = findViewById(R.id.textView9);
        ValuePRM = findViewById(R.id.textView10);
        ValueULT = findViewById(R.id.textView13);
        ValueMNN = findViewById(R.id.textView11);
        ValueACC = findViewById(R.id.textView14);
        ValueART = findViewById(R.id.textView15);

        ValueTotal = findViewById(R.id.totalRecordedOYS);

        /*progress dialog declare*/
        progressDialogGIF = new progressDialogGIF(this);

        /*Search feature*/
        search = (SearchView) findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                myAppAdapter.getFilter().filter(s);
                return false;
            }
        });

        /*Condition if theres and no record*/
        image = findViewById(R.id.noRecord);

        if(itemArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            image.setVisibility(LinearLayout.VISIBLE);

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            image.setVisibility(LinearLayout.GONE);
        }


        new oneYearSaleAsync().execute();

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

    class oneYearSaleAsync extends AsyncTask<Void, Void, Void> {

        List listExpiredALL = new ArrayList();

        List listExpiredATK = new ArrayList();
        List listExpiredPRL = new ArrayList();
        List listExpiredORL = new ArrayList();
        List listExpiredPRM = new ArrayList();
        List listExpiredULT = new ArrayList();
        List listExpiredMNN = new ArrayList();
        List listExpiredACC = new ArrayList();
        List listExpiredART = new ArrayList();


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * from gudang WHERE tglmasuk < DATE_SUB(NOW(),INTERVAL 1 YEAR)");

                while (rs.next()) {

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, -1); // to get previous year add -1
                    java.util.Date prevYear = cal.getTime();

                    /*take last updated value*/
                    Date val = rs.getDate(6);

                    /*Get All Value Recorded*/
                    if(rs.getString(6) != null) {
                        if(prevYear.compareTo(val) > 0) {
                            listExpiredALL.add(rs.getString(2));
                        }
                    } else {
                        listExpiredALL.add(rs.getString(2));
                    }

                    if(rs.getString(1).startsWith("ATK")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredATK.add(rs.getString(2));
                            }
                        } else {
                            listExpiredATK.add(rs.getString(2));
                        }
                    }

                    if(rs.getString(1).startsWith("PRL")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredPRL.add(rs.getString(2));
                            }
                        } else {
                            listExpiredPRL.add(rs.getString(2));
                        }
                    }

                    if(rs.getString(1).startsWith("ORL")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredORL.add(rs.getString(2));
                            }
                        } else {
                            listExpiredORL.add(rs.getString(2));
                        }
                    }

                    if(rs.getString(1).startsWith("PRM")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredPRM.add(rs.getString(2));
                            }
                        } else {
                            listExpiredPRM.add(rs.getString(2));
                        }
                    }

                    if(rs.getString(1).startsWith("ULT")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredULT.add(rs.getString(2));
                            }
                        } else {
                            listExpiredULT.add(rs.getString(2));
                        }
                    }

                    if(rs.getString(1).startsWith("MNN")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredMNN.add(rs.getString(2));
                            }
                        } else {
                            listExpiredMNN.add(rs.getString(2));
                        }
                    }

                    if(rs.getString(1).startsWith("AKS")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredACC.add(rs.getString(2));
                            }
                        } else {
                            listExpiredACC.add(rs.getString(2));
                        }
                    }

                    if(rs.getString(1).startsWith("ART")) {
                        if(rs.getString(6) != null) {
                            if(prevYear.compareTo(val) > 0) {
                                listExpiredART.add(rs.getString(2));
                            }
                        } else {
                            listExpiredART.add(rs.getString(2));
                        }
                    }

                }


            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ValueTotal.setText(String.valueOf(listExpiredALL.size()));

            ValueATK.setText(String.valueOf(listExpiredATK.size()));
            ValuePRL.setText(String.valueOf(listExpiredPRL.size()));
            ValueORL.setText(String.valueOf(listExpiredORL.size()));
            ValuePRM.setText(String.valueOf(listExpiredPRM.size()));
            ValueULT.setText(String.valueOf(listExpiredULT.size()));
            ValueMNN.setText(String.valueOf(listExpiredMNN.size()));
            ValueACC.setText(String.valueOf(listExpiredACC.size()));
            ValueART.setText(String.valueOf(listExpiredART.size()));


            super.onPostExecute(aVoid);
        }
    }


    class oneYearSaleRecycleView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            progressDialogGIF.showDialog();
            itemArrayList.clear();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * from gudang where id_barang LIKE \""+ query+"%\" AND tglmasuk < DATE_SUB(NOW(),INTERVAL 1 YEAR)");

                while (rs.next()) {

                    /*Formating*/
                    PrettyTime p = new PrettyTime(new Locale("EN"));

                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                    /*Set Calendar*/
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, -1); // to get previous year add -1
                    java.util.Date prevYear = cal.getTime();

                    Date val = rs.getDate(6);

                    if(rs.getString(6) != null) {
                        if (prevYear.compareTo(val) > 0) {
                            itemArrayList.add(new classListOneYearSale(rs.getString(2),rs.getString(3), p.format(rs.getDate(6)), rs.getString(7),formatRupiah.format(rs.getDouble(8))));
                        }
                    }else {
                        itemArrayList.add(new classListOneYearSale(rs.getString(2),rs.getString(3), p.format(rs.getDate(5)), rs.getString(7),formatRupiah.format(rs.getDouble(8))));
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(itemArrayList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                image.setVisibility(LinearLayout.VISIBLE);

            } else {
                recyclerView.setVisibility(View.VISIBLE);
                image.setVisibility(LinearLayout.GONE);
            }

            try
            {
                myAppAdapter = new oneYearSale.MyAppAdapter(itemArrayList , oneYearSale.this);
                recyclerView.setAdapter(myAppAdapter);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }


            myAppAdapter.notifyDataSetChanged();
            progressDialogGIF.stopDialog();
            super.onPostExecute(aVoid);
        }
    }

    public class MyAppAdapter extends RecyclerView.Adapter<oneYearSale.MyAppAdapter.ViewHolder> implements Filterable{
        private List<classListOneYearSale> searchItem;
        private List<classListOneYearSale> searchItemFull;

        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public image title and image url
            public TextView barang,barcode,last_update,harga_jual,stock;
            public View layout;

            public ViewHolder(View v)
            {
                super(v);
                layout = v;
                barang = (TextView) v.findViewById(R.id.oysBarang);
                barcode = (TextView) v.findViewById(R.id.oysBarcode);
                last_update = (TextView) v.findViewById(R.id.textView25);
                harga_jual = (TextView) v.findViewById(R.id.textView24);
                stock = (TextView) v.findViewById(R.id.textView26);

            }
        }


        // Constructor
        public MyAppAdapter(List<classListOneYearSale> myDataset, Context context)
        {

            searchItem = myDataset;
            this.context = context;

            searchItemFull = new ArrayList<>(myDataset);

        }

        // Create new views (invoked by the layout manager) and inflates
        @Override
        public oneYearSale.MyAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.recycleview_one_year_sale, parent, false);
            oneYearSale.MyAppAdapter.ViewHolder vh = new oneYearSale.MyAppAdapter.ViewHolder(v);
            return vh;

        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(oneYearSale.MyAppAdapter.ViewHolder holder, final int position) {

            final classListOneYearSale classListItems = searchItem.get(position);
            holder.barang.setText(classListItems.getBarang());
            holder.barcode.setText(classListItems.getBarcode());
            holder.last_update.setText(classListItems.getTanggal());
            holder.harga_jual.setText(classListItems.getHarga_jual());
            holder.stock.setText(classListItems.getStock());

        }

        @Override
        public int getItemCount() {
            return searchItem.size();
        }

        @Override
        public Filter getFilter() {
            return searchFilter;
        }

        private Filter searchFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                List <classListOneYearSale> filterList = new ArrayList<classListOneYearSale>();

                if(charSequence == null || charSequence.length() == 0 ) {
                    filterList.addAll(searchItemFull);
                }else {
                    String fillterPatern = charSequence.toString().toLowerCase();

                    for (classListOneYearSale item :  searchItemFull) {
                        if(item.getBarang().toLowerCase().contains(fillterPatern)) {
                            filterList.add(item);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = filterList;
                return results;

            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchItem.clear();
                searchItem.addAll((List) filterResults.values);

                if(searchItem.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    image.setVisibility(LinearLayout.VISIBLE);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    image.setVisibility(LinearLayout.GONE);
                }


                notifyDataSetChanged();
            }

        };

    }

    public void displayDataOYS(@NotNull View v) {
        switch (v.getId()) {
            case R.id.atk:
                query = "ATK";
                new oneYearSaleRecycleView().execute();
                break;
            case R.id.prl:
                query = "PRL";
                new oneYearSaleRecycleView().execute();
                break;
            case R.id.orl:
                query = "ORL";
                new oneYearSaleRecycleView().execute();
                break;
            case R.id.prm:
                query = "PRM";
                new oneYearSaleRecycleView().execute();
                break;
            case R.id.ult:
                query = "ULT";
                new oneYearSaleRecycleView().execute();
                break;
            case R.id.mnn:
                query = "MNN";
                new oneYearSaleRecycleView().execute();
                break;
            case R.id.acc:
                query = "AKS";
                new oneYearSaleRecycleView().execute();
                break;
            case R.id.art:
                query = "ART";
                new oneYearSaleRecycleView().execute();
                break;
        }
    }

}