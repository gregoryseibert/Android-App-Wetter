package de.gregoryseibert.wetter.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.gregoryseibert.wetter.R;
import de.gregoryseibert.wetter.util.Utility;

/**
 * Created by gs71756 on 17.10.2016.
 */

public class ForecastWidgetProviderBig extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for(int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_big);

            updateWeather(context, views);
            updateTime(views);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent createClockTickIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastWidgetKey = context.getString(R.string.pref_last_widget);
        prefs.edit().putLong(lastWidgetKey, System.currentTimeMillis()-Utility.NOTIFICATION_INTERVAL).apply();

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        long firstTime = System.currentTimeMillis();
        firstTime += (60000 - firstTime % 60000);

        alarmManager.setRepeating(AlarmManager.RTC, firstTime, 60000, createClockTickIntent(context));
    }

    public void updateWeather(Context context, RemoteViews views) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastWidgetKey = context.getString(R.string.pref_last_widget);
        long lastSync = prefs.getLong(lastWidgetKey, 0);
        long elapsedTime = System.currentTimeMillis() - lastSync;
        boolean nextSync = elapsedTime >= Utility.NOTIFICATION_INTERVAL;

        if (nextSync) {
            prefs.edit().putLong(lastWidgetKey, System.currentTimeMillis()).apply();

            String locationQuery = Utility.getPreferredLocation(context);
            Uri weatherUri = Utility.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());
            Cursor cursor = context.getContentResolver().query(weatherUri, Utility.NOTIFICATION_COLUMNS, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String desc = cursor.getString(Utility.COL_NOTIFICATION_SHORT_DESC);
                String maxTemp = Utility.formatTemperature(cursor.getDouble(Utility.COL_NOTIFICATION_MAX_TEMP), Utility.isMetric(context));
                String minTemp = Utility.formatTemperature(cursor.getDouble(Utility.COL_NOTIFICATION_MIN_TEMP), Utility.isMetric(context));
                int weatherIcon = Utility.getIcon(cursor.getString(Utility.COL_NOTIFICATION_WEATHER_ID));

                views.setImageViewResource(R.id.list_item_icon, weatherIcon);
                views.setTextViewText(R.id.list_item_forecast_textview, Utility.formatDescription(desc));
                views.setTextViewText(R.id.list_item_low_textview, minTemp);
                views.setTextViewText(R.id.list_item_high_textview, maxTemp);
            }

            if(cursor != null) {
                cursor.close();
            }
        }
    }

    public void updateTime(RemoteViews views) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        String currentDateandTime = sdf.format(new Date());
        views.setTextViewText(R.id.list_item_date_textview, currentDateandTime);
    }
}
