package com.example.atkmobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text=(TextView)findViewById(R.id.textView4);
        new checkData().execute();

        /*show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               testDB();
            }
        });*/

    }

    public void lihatPendapatan(View view) {
        Intent intent = new Intent(this, DailyIncome_2.class);
        startActivity(intent);
    }

    public void lihatGudang(View view) {
        Intent intent = new Intent(this, Warehouse.class);
        startActivity(intent);
    }




    class checkData extends AsyncTask<Void, Void, Void> {
        String result= "";

        /*Format Rupiah*/
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("your db pass and username here");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select sum(total) as income from datatransaksi WHERE DATE(tanggal) = CURRENT_DATE");
                ResultSetMetaData rmd = rs.getMetaData();

                while (rs.next()) {
                    result +=  formatRupiah.format(rs.getDouble(1));
                }
            }catch (Exception e){
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            text.setText(result);
            super.onPostExecute(aVoid);
        }
    }


}