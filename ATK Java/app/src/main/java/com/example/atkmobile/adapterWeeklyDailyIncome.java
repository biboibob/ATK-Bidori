package com.example.atkmobile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class adapterWeeklyDailyIncome extends PagerAdapter  {

    private List<classListWeekDetailDI> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public adapterWeeklyDailyIncome(List<classListWeekDetailDI> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycleview_detail_dailyincome, container, false);

        TextView total,range,transaction;

        total = view.findViewById(R.id.textView33);
        range = view.findViewById(R.id.textView35);
        transaction = view.findViewById(R.id.textView36);

        total.setText(models.get(position).getTotal());
        range.setText(models.get(position).getDate());
        transaction.setText(models.get(position).getOrder());

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("param", models.get(position).getTitle());
                context.startActivity(intent);
                // finish();
            }
        });*/

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        /*super.destroyItem(container, position, object);*/
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
