package de.gregoryseibert.wetter.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.gregoryseibert.wetter.R;
import de.gregoryseibert.wetter.helper.Utility;

/**
 * Created by gs71756 on 10.10.2016.
 */

public class ForecastAdapter extends CursorAdapter {
    private Context context;

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case Utility.VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case Utility.VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String weatherId = cursor.getString(Utility.COL_FORECAST_WEATHER_CONDITION_ID);
        viewHolder.iconView.setImageResource(Utility.getIcon(weatherId));

        long dateInMillis = cursor.getLong(Utility.COL_FORECAST_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getDateString(dateInMillis));

        String description = cursor.getString(Utility.COL_FORECAST_WEATHER_DESC);
        viewHolder.descriptionView.setText(Utility.formatDescription(description));

        boolean isMetric = Utility.isMetric(context);

        double high = cursor.getDouble(Utility.COL_FORECAST_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(high, isMetric));

        double low = cursor.getDouble(Utility.COL_FORECAST_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(low, isMetric));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? Utility.VIEW_TYPE_TODAY : Utility.VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return Utility.VIEW_TYPE_COUNT;
    }
}