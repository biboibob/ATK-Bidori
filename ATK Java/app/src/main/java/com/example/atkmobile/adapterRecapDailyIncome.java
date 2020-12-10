package com.example.atkmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class adapterRecapDailyIncome extends RecyclerView.Adapter<adapterRecapDailyIncome.ViewHolder>{
    private List<classListRecapDI> recap;
    public Context context;
    public Double avg;


    // Create new views (invoked by the layout manager) and inflates
    @Override
    public adapterRecapDailyIncome.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycleview_recap_daily_income, parent, false);
        /*adapterRecapDailyIncome.ViewHolder vh = new adapterRecapDailyIncome.ViewHolder(v);
        return vh;*/
        return new adapterRecapDailyIncome.ViewHolder(v);

    }




    // Binding items to the view
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        final classListRecapDI classListItems = recap.get(position);
        holder.total.setText(classListItems.getTotal());
        holder.date.setText(classListItems.getDate());

        if(classListItems.getVal() < avg) {
            holder.graphImg.setImageResource(R.drawable.icon_lower_than_v2);
        } else {
            holder.graphImg.setImageResource(R.drawable.icon_higher_than_v2);
        }


    }

    @Override
    public int getItemCount() {
        return recap.size();
    }

    public adapterRecapDailyIncome(List<classListRecapDI> myDataset, Context context, Double varAverage) {
        this.recap = myDataset;
        this.context = context;
        this.avg = varAverage;
        /*searchItemFull = new ArrayList<>(myDataset);*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // public image title and image url
        public TextView total,date;
        public ImageView graphImg;
        public View layout;

        public ViewHolder(View v)
        {
            super(v);
            layout = v;
            total = (TextView) v.findViewById(R.id.totalRecap);
            date = (TextView) v.findViewById(R.id.dateRecap);
            graphImg = (ImageView)v.findViewById(R.id.imgGraph);

        }
    }


}


