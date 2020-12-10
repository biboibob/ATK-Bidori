package com.example.atkmobile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailStorage extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList itemArrayList = new ArrayList<String>();  //List items Array
    private ArrayList itemArrayListPIC = new ArrayList<String>();  //List items Array

    /*progressDialog*/

    private MyPICAppAdapter myPICAppAdapter;
    private RecyclerView recyclerViewPIC; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManagerPIC;

    private MyAppAdapter myAppAdapter; //Array Adapter
    private RecyclerView recyclerView; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    SearchView search;

    TextView itemRecorded, storageZero,storageDiscount,getTitle;

    String query,title;

    private DrawerLayout drawer;

    progressDialogGIF progressDialogGIF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_storage);

        /*Memunculkan Action Bar*/
        Toolbar toolbar = findViewById(R.id.toolbarDetailStorage);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        /*Drawer*/
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navViewDetailStorage);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        /*Navigation Drawer Menu*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*RecycleView*/
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        itemArrayList =  new ArrayList(); //arraylist barang

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

        //Header Section
        itemRecorded = findViewById(R.id.itemRecorded);
        storageZero = findViewById(R.id.storageZero);
        storageDiscount = findViewById(R.id.storageDiscount);

        /*PIC Recycleview*/
        /*RecycleView*/
        recyclerViewPIC = (RecyclerView) findViewById(R.id.recyclerViewPIC);
        recyclerViewPIC.setHasFixedSize(true);
        mLayoutManagerPIC = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewPIC.setLayoutManager(mLayoutManagerPIC);

        itemArrayListPIC =  new ArrayList(); //arraylist barang

        //Get Intent Session
        Intent intent = getIntent();
        query = intent.getStringExtra("SESSION");
        title = intent.getStringExtra("SESSION_TITLE");

        getTitle = findViewById(R.id.kategoriTitle);

        /*progress dialog declare*/
        progressDialogGIF = new progressDialogGIF(this);


        new detailStorage().execute();
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

    class detailStorage extends AsyncTask<Void, Void, Void> {

        String rsItemRecord,rsStorageZero,rsStorageDiscount;
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
                /*ResultSet rs = stmt.executeQuery("SELECT id_barang,barang,barcode,penginput,tgl_update_akhir,stock,harga_jual from gudang WHERE id_barang LIKE \""+ query +"%\" ORDER BY id_barang ASC;");*/
                ResultSet rs = stmt.executeQuery("SELECT (SELECT COUNT(BARANG) from gudang where id_barang LIKE \""+query+"%\") as totalBarang,(SELECT COUNT(stock) from gudang where stock = 0 and id_barang LIKE \""+query+"%\")as totalZero,(SELECT COUNT(diskon) from gudang where diskon > 0 and id_barang LIKE \""+query+"%\")as totalDiskon,penginput from gudang WHERE id_barang LIKE \""+query+"%\" GROUP BY penginput ASC");

                while (rs.next()) {
                    rsItemRecord = rs.getString(1);
                    rsStorageZero = rs.getString(2);
                    rsStorageDiscount = rs.getString(3);

                    itemArrayListPIC.add(new classListPIC(rs.getString(4)));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                /*ResultSet rs = stmt.executeQuery("SELECT id_barang,barang,barcode,penginput,tgl_update_akhir,stock,harga_jual from gudang WHERE id_barang LIKE \""+ query +"%\" ORDER BY id_barang ASC;");*/
                ResultSet rs = stmt.executeQuery("SELECT id_barang,barang,barcode,penginput,tgl_update_akhir,stock,harga_jual from gudang WHERE id_barang LIKE \""+query+"%\" ORDER BY id_barang ASC;");

                while (rs.next()) {

                    PrettyTime p = new PrettyTime(new Locale("EN"));

                    /*Format Rupiah*/
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);


                    itemArrayList.add(new classListDetailStorage(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),p.format(rs.getDate(5)),rs.getString(6),formatRupiah.format(rs.getDouble(7))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            itemRecorded.setText(rsItemRecord);
            storageZero.setText(rsStorageZero);
            storageDiscount.setText(rsStorageDiscount);

            getTitle.setText(title);

            try
            {
                myAppAdapter = new MyAppAdapter(itemArrayList , DetailStorage.this);
                recyclerView.setAdapter(myAppAdapter);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            try
            {
                myPICAppAdapter = new MyPICAppAdapter(itemArrayListPIC , DetailStorage.this);
                recyclerViewPIC.setAdapter(myPICAppAdapter);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            myPICAppAdapter.notifyDataSetChanged();
            myAppAdapter.notifyDataSetChanged();

            progressDialogGIF.stopDialog();
            super.onPostExecute(aVoid);
        }
    }

    public class MyAppAdapter extends RecyclerView.Adapter<DetailStorage.MyAppAdapter.ViewHolder> implements Filterable {
        private List<classListDetailStorage> searchItem;
        private List<classListDetailStorage> searchItemFull;

        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public image title and image url
            public TextView barang,barcode,penginput,last_update,harga_jual,stock;
            public View layout;

            public ViewHolder(View v)
            {
                super(v);
                layout = v;
                barang = (TextView) v.findViewById(R.id.dsNamaBarang);
                barcode = (TextView) v.findViewById(R.id.dsBarcode);
                penginput = (TextView) v.findViewById(R.id.dsPenginput);
                last_update = (TextView) v.findViewById(R.id.dsLastUpdate);
                harga_jual = (TextView) v.findViewById(R.id.dsHargaJual);
                stock = (TextView) v.findViewById(R.id.dsStock);

            }
        }


        // Constructor
        public MyAppAdapter(List<classListDetailStorage> myDataset, Context context)
        {

            searchItem = myDataset;
            this.context = context;

            searchItemFull = new ArrayList<>(myDataset);

        }

        // Create new views (invoked by the layout manager) and inflates
        @Override
        public DetailStorage.MyAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.recycleview_detail_storage, parent, false);
            MyAppAdapter.ViewHolder vh = new MyAppAdapter.ViewHolder(v);
            return vh;

        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(DetailStorage.MyAppAdapter.ViewHolder holder, final int position) {

            final classListDetailStorage classListItems = searchItem.get(position);
            holder.barang.setText(classListItems.getBarang());
            holder.barcode.setText(classListItems.getBarcode());
            holder.penginput.setText(classListItems.getPenginput());
            holder.last_update.setText(classListItems.getUpdate_akkhir());
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

                List <classListDetailStorage> filterList = new ArrayList<classListDetailStorage>();

                if(charSequence == null || charSequence.length() == 0 ) {
                    filterList.addAll(searchItemFull);
                }else {
                    String fillterPatern = charSequence.toString().toLowerCase();

                    for (classListDetailStorage item :  searchItemFull) {
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
                notifyDataSetChanged();
            }

        };

    }

    public class MyPICAppAdapter extends RecyclerView.Adapter<DetailStorage.MyPICAppAdapter.ViewHolder> {
        private List<classListPIC> searchItem;
        private List<classListPIC> searchItemFull;

        public Context context;

        public MyPICAppAdapter(List<classListPIC> myDataset, Context context)
        {
            searchItem = myDataset;
            this.context = context;

            searchItemFull = new ArrayList<>(myDataset);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // public image title and image url
            public TextView person;
            public View layout;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                person = (TextView) v.findViewById(R.id.personName);

            }
        }


        // Create new views (invoked by the layout manager) and inflates
        @Override
        public DetailStorage.MyPICAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.recycleview_pic, parent, false);
            MyPICAppAdapter.ViewHolder vh = new MyPICAppAdapter.ViewHolder(v);
            return vh;

        }


        // Binding items to the view
        @Override
        public void onBindViewHolder(DetailStorage.MyPICAppAdapter.ViewHolder holder, final int position) {

            final classListPIC classListItems = searchItem.get(position);
            holder.person.setText(classListItems.getPerson());

        }

        @Override
        public int getItemCount() {
            return searchItem.size();
        }
    }
}