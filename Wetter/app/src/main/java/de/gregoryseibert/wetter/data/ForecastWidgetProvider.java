package de.gregoryseibert.wetter.data;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.gregoryseibert.wetter.R;
import de.gregoryseibert.wetter.helper.Utility;

/**
 * Created by gs71756 on 14.10.2016.
 */

public class ForecastWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastWidgetKey = context.getString(R.string.pref_last_widget);
        long lastSync = prefs.getLong(lastWidgetKey, 0);

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            if (System.currentTimeMillis() - lastSync >= Utility.NOTIFICATION_INTERVAL) {
                String locationQuery = Utility.getPreferredLocation(context);
                Uri weatherUri = Utility.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());
                Cursor cursor = context.getContentResolver().query(weatherUri, Utility.NOTIFICATION_COLUMNS, null, null, null);

                if (cursor.moveToFirst()) {
                    String desc = cursor.getString(Utility.COL_NOTIFICATION_SHORT_DESC);
                    String maxTemp = Utility.formatTemperature(cursor.getDouble(Utility.COL_NOTIFICATION_MAX_TEMP), Utility.isMetric(context));
                    String minTemp = Utility.formatTemperature(cursor.getDouble(Utility.COL_NOTIFICATION_MIN_TEMP), Utility.isMetric(context));
                    int weatherIcon = Utility.getIcon(cursor.getString(Utility.COL_NOTIFICATION_WEATHER_ID));

                    views.setImageViewResource(R.id.list_item_icon, weatherIcon);
                    views.setTextViewText(R.id.list_item_forecast_textview, Utility.formatDescription(desc));
                    views.setTextViewText(R.id.list_item_low_textview, minTemp);
                    views.setTextViewText(R.id.list_item_high_textview, maxTemp);
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String currentDateandTime = sdf.format(new Date());
            views.setTextViewText(R.id.list_item_date_textview, currentDateandTime);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
