package de.gregoryseibert.wetter.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.gregoryseibert.wetter.helper.Utility;
import de.gregoryseibert.wetter.model.Forecast;
import de.gregoryseibert.wetter.R;

/**
 * Created by gs71756 on 10.10.2016.
 */

public class ForecastAdapter extends ArrayAdapter<Forecast>{
    Context context;
    int forecastLayoutId, todayForecastLayoutId;
    ArrayList<Forecast> data = null;

    public ForecastAdapter(Context context, int layoutResourceId, int todayLayoutResourceId, ArrayList<Forecast> data) {
        super(context, layoutResourceId, data);
        this.forecastLayoutId = layoutResourceId;
        this.todayForecastLayoutId = todayLayoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ForecastHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            if(position==0) {
                row = inflater.inflate(todayForecastLayoutId, parent, false);
            } else {
                row = inflater.inflate(forecastLayoutId, parent, false);
                if(position%2==0) {
                    row.setBackgroundColor(Utility.ALTERNATED_ROW_BACKGROUND_COLOR);
                }
            }

            holder = new ForecastHolder();
            holder.icon = (ImageView)row.findViewById(R.id.list_item_icon);
            holder.date = (TextView)row.findViewById(R.id.list_item_date_textview);
            holder.forecast = (TextView)row.findViewById(R.id.list_item_forecast_textview);
            holder.min = (TextView)row.findViewById(R.id.list_item_low_textview);
            holder.max = (TextView)row.findViewById(R.id.list_item_high_textview);

            row.setTag(holder);
        } else {
            holder = (ForecastHolder)row.getTag();
        }

        try {
            Forecast forecast = data.get(position);

            holder.icon.setImageDrawable(context.getResources().getDrawable(forecast.getIcon()));

            if(position==0) {
                holder.date.setText(Utility.TODAY_TEXT);
            } else if(position==1) {
                holder.date.setText(Utility.TOMORROW_TEXT);
            } else {
                holder.date.setText(forecast.getDate());
            }

            holder.forecast.setText(forecast.getDescription());
            holder.min.setText(forecast.getMin());
            holder.max.setText(forecast.getMax());
        }catch (IndexOutOfBoundsException e) {
            Log.e("", e.getMessage());
        }

        return row;
    }

    private static class ForecastHolder {
        ImageView icon;
        TextView date;
        TextView forecast;
        TextView min;
        TextView max;
    }
}
