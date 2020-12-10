package com.example.atkmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;

import com.anychart.core.annotations.Line;
import com.anychart.scales.Linear;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.MPPointD;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;


import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyIncome_2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    EditText startDay, endDay;
    String queryStart, queryEnd;
    Double varAverage;

    TextView date,income,minimum,maximum,average,paramIncome,paramBotIncome;
    ImageView imageIncome;
    LinearLayout imageParamIncome;
    LineChart chart;

    DotsIndicator dotsIndicator;

    /*view pager*/
    ViewPager viewPager;
    adapterWeeklyDailyIncome adapterWeeklyDailyIncome;
    List<classListWeekDetailDI> models;

    private ArrayList recap = new ArrayList<String>();  //List items Array
    private adapterRecapDailyIncome adapterRecap;
    private RecyclerView recycleRecap;
    BottomSheetBehavior bottomSheetBehavior;




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

        new checkData().execute();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_income_2_0);


        /*Memunculkan tanggal datepicker*/
        startDay=(EditText) findViewById(R.id.chooseDateStart);
        startDay.setInputType(InputType.TYPE_NULL);

        endDay=(EditText) findViewById(R.id.chooseDateEnd);
        endDay.setInputType(InputType.TYPE_NULL);

        /*Set Var Value*/
        income=(TextView)findViewById(R.id.textView27);
        date = (TextView)findViewById(R.id.dateIncome);
        minimum = (TextView)findViewById(R.id.textView31);
        maximum=(TextView)findViewById(R.id.textView30);
        average=(TextView)findViewById(R.id.avgIncome);

        /*Clicked line graph value*/
        imageIncome = (ImageView)findViewById(R.id.imgInc);
        paramIncome = (TextView)findViewById(R.id.textView29);
        paramBotIncome = (TextView)findViewById(R.id.bottomParam);

        imageParamIncome = (LinearLayout) findViewById(R.id.noRecordSelectedDI);
        imageParamIncome.setVisibility(View.VISIBLE);

        final ConstraintLayout consChartVal = (ConstraintLayout)findViewById(R.id.consChartVal);
        consChartVal.setVisibility(View.GONE);

        /*array list for recap income*/
        recap =  new ArrayList(); //arraylist barang
        recycleRecap = findViewById(R.id.recyclerViewRecap);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DailyIncome_2.this);
        recycleRecap.setLayoutManager(layoutManager);

        /*bottomsheet config*/
        LinearLayout bottomSheetLayout = findViewById(R.id.linearLayoutbtmSheet);
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        /*set View Pager*/
        models = new ArrayList();
        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setPageMargin(20);


        /*BottomSheet*/
        FloatingActionButton btnAction = findViewById(R.id.fab);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
        });

        /*navigation*/

        /*Memunculkan Action Bar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        /*Drawer*/
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        /*Navigation Drawer Menu*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        /*Chart*/
        chart = (LineChart) findViewById(R.id.chartline);
        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                float tappedX = me.getX();
                float tappedY = me.getY();
                MPPointD point = chart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(tappedX, tappedY);

                double xValue = point.x;
                double yValue = point.y;

                consChartVal.setVisibility(View.VISIBLE);
                imageParamIncome.setVisibility(View.GONE);

                Date dates = new Date(((long)xValue)*1000L);
                SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MMMM-yyyy");
                date.setText(originalFormat.format(dates));

                /*Format Rupiah*/
                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                income.setText(formatRupiah.format(yValue));

                if(yValue < varAverage) {
                    imageIncome.setImageResource(R.drawable.icon_lower_than);
                    paramIncome.setText("Lower");
                    paramBotIncome.setText("Than Average");
                    paramIncome.setTextColor(Color.parseColor("#EF6363"));
                    paramBotIncome.setTextColor(Color.parseColor("#EF6363"));
                } else {
                    imageIncome.setImageResource(R.drawable.icon_higher_than);
                    paramIncome.setText("Higher");
                    paramBotIncome.setText("Than Average");
                    paramIncome.setTextColor(Color.parseColor("#71CE86"));
                    paramBotIncome.setTextColor(Color.parseColor("#71CE86"));
                }

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

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

                                new checkData().execute();
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

                                new checkData().execute();

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




    class checkData extends AsyncTask<Void, Void, Void> {
        String resultIncome= "", resultDate ="",resultminimum="",resultmaximum="",resultaverage="";

        /*ArrayForChart*/
        List tanggalPendapatanPerhari = new ArrayList();
        List incomes = new ArrayList();

        /*Format Rupiah*/
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        /*Format time*/
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd");


        @Override
        protected void onPreExecute() {
            models.clear();
            recap.clear();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select tanggal, sum(total) as income from datatransaksi WHERE tanggal BETWEEN \""+ queryStart +"\" AND \""+ queryEnd +"\" GROUP BY tanggal");

                while (rs.next()) {

                    recap.add(new classListRecapDI(originalFormat.format(rs.getDate(1)),formatRupiah.format(rs.getDouble(2)),rs.getDouble(2)));

                    resultDate += rs.getDate(1) + "\n";
                    resultIncome +=  formatRupiah.format(rs.getDouble(2)) + "\n";

                    tanggalPendapatanPerhari.add(rs.getDate(1).getTime()/1000);
                    incomes.add(rs.getFloat(2));

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("select MAX(income) as maximum, MIN(income) as minimum, ROUND(AVG(income),0) as average from (select sum(total) as income from datatransaksi WHERE tanggal BETWEEN \""+ queryStart+"\" AND \""+ queryEnd +"\" GROUP BY tanggal)as total");

                while(rs.next()) {

                    varAverage = rs.getDouble(3);

                    resultmaximum += formatRupiah.format(rs.getDouble(1));
                    resultminimum += formatRupiah.format(rs.getDouble(2));
                    resultaverage += formatRupiah.format(rs.getDouble(3));
                }
            }catch (Exception e ) {
                e.printStackTrace();
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT CONCAT(datatransaksi.tanggal, ' - ', datatransaksi.tanggal + INTERVAL 6 DAY) AS date,SUM(total)as total,COUNT(kode_order) as kode_order FROM datatransaksi WHERE datatransaksi.tanggal BETWEEN \""+queryStart+"\" AND \""+queryEnd+"\" GROUP BY WEEK(tanggal)");

                while(rs.next()) {
                    models.add(new classListWeekDetailDI(rs.getString(1),formatRupiah.format(rs.getDouble(2)), rs.getString(3)));
                }
            }catch (Exception e ) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {

                /*set adapter weekly*/
                adapterWeeklyDailyIncome = new adapterWeeklyDailyIncome(models,DailyIncome_2.this);
                viewPager.setAdapter(adapterWeeklyDailyIncome);
                viewPager.setSaveFromParentEnabled(false);
                viewPager.setSaveEnabled(false);
                dotsIndicator.setViewPager(viewPager);

            }catch (Exception e) {
                e.printStackTrace();
            }

            try {
                adapterRecap = new adapterRecapDailyIncome(recap,DailyIncome_2.this, varAverage);
                recycleRecap.setAdapter(adapterRecap);

            }catch (Exception e) {
                e.printStackTrace();
            }

            adapterRecap.notifyDataSetChanged();
            adapterWeeklyDailyIncome.notifyDataSetChanged();

            maximum.setText(resultmaximum);
            minimum.setText(resultminimum);
            average.setText(resultaverage);


            /*make chart no padding or margin*/
            chart.setViewPortOffsets(0f, 0f, 0f, 0f);

            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new DateValueFormatter());
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawLabels(false);

            YAxis yAxis = chart.getAxisRight();
            yAxis.setDrawAxisLine(false);
            yAxis.setDrawLabels(false);

            YAxis yAxis2 = chart.getAxisLeft();
            yAxis2.setDrawAxisLine(false);
            yAxis2.setDrawLabels(false);
            yAxis2.setValueFormatter(new DataValueFormatY());
            /*yAxis2.setZeroLineColor(Color.LTGRAY);
            yAxis2.setTextColor(Color.LTGRAY);
            yAxis2.setTextSize(5);*/


            List<Entry> entries = new ArrayList<Entry>();

            for (int i = 0; i < incomes.size(); i++){
                entries.add(new Entry(tanggalPendapatanPerhari.get(i).hashCode(), (Float) incomes.get(i)));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Label");

            Drawable drawable = ContextCompat.getDrawable(DailyIncome_2.this, R.drawable.line_fade);
            dataSet.setFillDrawable(drawable);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawFilled(true);
            dataSet.setDrawCircles(false);
            dataSet.setLineWidth(3);

            dataSet.setColor(Color.CYAN);
            dataSet.setValueTextColor(Color.LTGRAY);
            dataSet.setValueTextSize(16f);
            dataSet.setDrawValues(false);

            LineData lineData = new LineData(dataSet);

            Legend legend = chart.getLegend();
            legend.setEnabled(false);

            /*Set the Chart Attribute*/
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisRight().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);
            chart.setDrawBorders(false);


            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.animateX(1000, Easing.EasingOption.Linear);
            chart.getDescription().setEnabled(false);

            chart.setData(lineData);
            chart.invalidate(); // refresh

            super.onPostExecute(aVoid);
        }


        public class DateValueFormatter implements IAxisValueFormatter {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // Simple version. You should use a DateFormatter to specify how you want to textually represent your date.

                Date date = new Date(((long)value)*1000L);
                SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                return originalFormat.format(date);

            }
            // ...
        }



        public class DataValueFormatY implements  IAxisValueFormatter {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Double val = new Double((long)value);

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                return formatRupiah.format(val);

            }
        }

        /*private class CustomDataEntry extends ValueDataEntry {

            CustomDataEntry(String x, Number value) {
                super(x, value);
            *//*    setValue("value2", value2);
                setValue("value3", value3);*//*
            }

        }*/
    }

    /*public class adapterRecapDailyIncome extends RecyclerView.Adapter<adapterRecapDailyIncome.ViewHolder>{
        private List<classListRecapDI> models;

        public Context context;

        public adapterRecapDailyIncome(List<classListRecapDI> myDataset, Context context) {
            this.models = myDataset;
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public image title and image url
            public TextView total,date;
            public View layout;

            public ViewHolder(View v)
            {
                super(v);
                layout = v;
                total = (TextView) v.findViewById(R.id.totalRecap);
                date = (TextView) v.findViewById(R.id.dateRecap);

            }
        }



        // Create new views (invoked by the layout manager) and inflates
        @Override
        public adapterRecapDailyIncome.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.recycleview_recap_daily_income, parent, false);
            DailyIncome_2.adapterRecapDailyIncome.ViewHolder vh = new DailyIncome_2.adapterRecapDailyIncome.ViewHolder(v);
            return vh;

        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(adapterRecapDailyIncome.ViewHolder holder, final int position) {

            final classListRecapDI classListItems = models.get(position);
            holder.total.setText(classListItems.getTotal());
            holder.date.setText(classListItems.getDate());


        }

        @Override
        public int getItemCount() {
            return models.size();
        }


    }*/

}







