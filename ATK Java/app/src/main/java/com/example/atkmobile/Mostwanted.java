package com.example.atkmobile;

import android.content.Context;
import android.content.Intent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Mostwanted extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList itemArrayList = new ArrayList<String>();  //List items Array


    private MyAppAdapter myAppAdapter; //Array Adapter
    private RecyclerView recyclerView; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;

    private DrawerLayout drawer;

    EditText startDay, endDay;
    String queryStart, queryEnd;
    SearchView search;

    LinearLayout parentResult;

    TextView barang1,barang2,barang3,barcode1,barcode2,barcode3,sold1,sold2,sold3;

    progressDialogGIF progressDialogGIF;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {

        //////////////////////////////////// Default Day  //////////////////////////////////////////

        Date dateDef = new Date();
        LocalDate localDate = dateDef.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int dayStart = 1;
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        int year = localDate.getYear();

        String monthStringStart = new DateFormatSymbols().getMonths()[month-1];
        String monthStringEnd = new DateFormatSymbols().getMonths()[month-1];

        queryStart = year + "-" + month + "-" + dayStart;
        queryEnd = year + "-" + month + "-" + day;

        startDay.setText(dayStart + " " + monthStringStart + "," + year);
        endDay.setText(day + " " + monthStringEnd + "," + year);

        new mostWantedData().execute();

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostwanted);

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

        /*1st 2nd 3rd*/
        barang1 = (TextView) findViewById(R.id.barang1);
        barang2 = (TextView) findViewById(R.id.barang2);
        barang3 = (TextView) findViewById(R.id.barang3);

        sold1 = (TextView) findViewById(R.id.sold1);
        sold2 = (TextView) findViewById(R.id.sold2);
        sold3 = (TextView) findViewById(R.id.sold3);

        barcode1 = (TextView) findViewById(R.id.barcode1);
        barcode2 = (TextView) findViewById(R.id.barcode2);
        barcode3 = (TextView) findViewById(R.id.barcode3);

        /*Result Loop*/
        parentResult = findViewById(R.id.parentResult);

        /*RecycleView*/
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        itemArrayList =  new ArrayList(); //arraylist barang

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


        /*Mengganti range tanggal*/
        startDay=(EditText) findViewById(R.id.chooseDateStart);
        startDay.setInputType(InputType.TYPE_NULL);

        endDay=(EditText) findViewById(R.id.chooseDateEnd);
        endDay.setInputType(InputType.TYPE_NULL);

        startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                        new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                       int yearStart, int monthStart,
                                                       int dayStart, int yearEnd,
                                                       int monthEnd, int dayEnd) {

                                String monthStringStart = new DateFormatSymbols().getMonths()[monthStart];
                                String monthStringEnd = new DateFormatSymbols().getMonths()[monthEnd];

                                queryStart = yearStart + "-" + (monthStart+1) + "-" + dayStart;
                                queryEnd = yearEnd + "-" + (monthEnd+1) + "-" + dayEnd;

                                startDay.setText(dayStart + " " + monthStringStart + "," + yearStart);
                                endDay.setText(dayEnd + " " + monthStringEnd + "," + yearEnd);

                                new mostWantedData().execute();


                            }
                        });

                smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
                smoothDateRangePickerFragment.setThemeDark(true);
            }
        });

        endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                        new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                       int yearStart, int monthStart,
                                                       int dayStart, int yearEnd,
                                                       int monthEnd, int dayEnd) {

                                String monthStringStart = new DateFormatSymbols().getMonths()[monthStart];
                                String monthStringEnd = new DateFormatSymbols().getMonths()[monthEnd];

                                queryStart = yearStart + "-" + (monthStart+1) + "-" + dayStart;
                                queryEnd = yearEnd + "-" + (monthEnd+1) + "-" + dayEnd;

                                startDay.setText(dayStart + " " + monthStringStart + "," + yearStart);
                                endDay.setText(dayEnd + " " + monthStringEnd + "," + yearEnd);

                                new mostWantedData().execute();

                            }
                        });

                smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
                smoothDateRangePickerFragment.setThemeDark(true);
            }
        });


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

    class mostWantedData extends AsyncTask<Void, Void, Void> {

        String barangSatu,barangDua,barangTiga,barcodeSatu,barcodeDua,barcodeTiga,soldSatu,soldDua,soldTiga;


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
                ResultSet rs = stmt.executeQuery("SELECT barang, barcode, SUM(Jumlah) as total_barang from dataorder WHERE tanggal between \""+ queryStart +"\" AND \""+ queryEnd +"\" group by barang ORDER BY total_barang DESC Limit 3");

                while (rs.next()) {
                    barangSatu = rs.getString(1);
                    barcodeSatu = rs.getString(2);
                    soldSatu = rs.getString(3);

                    rs.next();

                    barangDua = rs.getString(1);
                    barcodeDua = rs.getString(2);
                    soldDua = rs.getString(3);

                    rs.next();

                    barangTiga = rs.getString(1);
                    barcodeTiga = rs.getString(2);
                    soldTiga = rs.getString(3);


                }

            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT barang, barcode, SUM(Jumlah) as total_barang from dataorder WHERE tanggal between \""+ queryStart +"\" AND \""+ queryEnd +"\" group by barang ORDER BY total_barang DESC");

                while (rs.next()) {

                    itemArrayList.add(new classListMostWanted(rs.getString(1),rs.getString(2),rs.getString(3)));
                }


            }catch (Exception e){
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {



            barang1.setText(barangSatu);
            barcode1.setText(barcodeSatu);
            sold1.setText(soldSatu);

            barang2.setText(barangDua);
            barcode2.setText(barcodeDua);
            sold2.setText(soldDua);

            barang3.setText(barangTiga);
            barcode3.setText(barcodeTiga);
            sold3.setText(soldTiga);

            try
            {
                myAppAdapter = new MyAppAdapter(itemArrayList , Mostwanted.this);
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


    public class MyAppAdapter extends RecyclerView.Adapter<MyAppAdapter.ViewHolder> implements Filterable {
        private List<classListMostWanted> searchItem;
        private List<classListMostWanted> searchItemFull;

        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public image title and image url
            public TextView barang,barcode,sold;
            public View layout;

            public ViewHolder(View v)
            {
                super(v);
                layout = v;
                barang = (TextView) v.findViewById(R.id.listBarang);
                barcode = (TextView) v.findViewById(R.id.listBarcode);
                sold = (TextView) v.findViewById(R.id.listSold);

            }
        }


        // Constructor
        public MyAppAdapter(List<classListMostWanted> myDataset, Context context)
        {

            searchItem = myDataset;
            this.context = context;

            searchItemFull = new ArrayList<>(myDataset);

        }

        // Create new views (invoked by the layout manager) and inflates
        @Override
        public MyAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.recycleview_mostwanted, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final classListMostWanted classListItems = searchItem.get(position);
            holder.barang.setText(classListItems.getBarang());
            holder.barcode.setText(classListItems.getBarcode());
            holder.sold.setText(classListItems.getSold());

            /*Picasso.with(context).load(classListItems.getImg()).into(holder.imageView);*/
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

                List <classListMostWanted> filterList = new ArrayList<classListMostWanted>();

                if(charSequence == null || charSequence.length() == 0 ) {
                    filterList.addAll(searchItemFull);
                }else {
                    String fillterPatern = charSequence.toString().toLowerCase();

                    for (classListMostWanted item :  searchItemFull) {
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


}