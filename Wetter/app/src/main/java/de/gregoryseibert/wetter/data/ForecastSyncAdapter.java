package de.gregoryseibert.wetter.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import de.gregoryseibert.wetter.R;
import de.gregoryseibert.wetter.activity.MainActivity;
import de.gregoryseibert.wetter.util.Utility;

/**
 * Created by gs71756 on 14.10.2016.
 */

public class ForecastSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = ForecastSyncAdapter.class.getSimpleName();

    public ForecastSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String locationQuery = Utility.getPreferredLocation(getContext());

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr;

        try {
            Uri builtUri = Uri.parse(Utility.OWM_FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(Utility.OWM_QUERY_PARAM, locationQuery)
                    .appendQueryParameter(Utility.OWM_FORMAT_PARAM, Utility.OWM_FORMAT)
                    .appendQueryParameter(Utility.OWM_LANG_PARAM, Utility.OWM_LANG)
                    .appendQueryParameter(Utility.OWM_UNIT_PARAM, Utility.OWM_UNIT)
                    .appendQueryParameter(Utility.OWM_DAYS_PARAM, Integer.toString(Utility.OWM_NUM_DAYS))
                    .appendQueryParameter(Utility.OWM_APPID_PARAM, Utility.OWM_APIKEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(Utility.OWM_REQUEST_METHOD);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }

                if (builder.length() != 0) {
                    forecastJsonStr = builder.toString();
                    getWeatherDataFromJson(forecastJsonStr, locationQuery);
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        notifyWeather();
    }

    private void getWeatherDataFromJson(String forecastJsonStr, String locationSetting) throws JSONException {
        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(Utility.OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(Utility.OWM_CITY);
            String cityName = cityJson.getString(Utility.OWM_CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(Utility.OWM_COORD);
            double cityLatitude = cityCoord.getDouble(Utility.OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(Utility.OWM_LONGITUDE);

            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            dayTime = new Time();

            for(int i = 0; i < weatherArray.length(); i++) {
                long dateTime;;
                int humidity;
                double pressure, windSpeed, windDirection, high, low;
                String description, weatherId;

                JSONObject dayForecast = weatherArray.getJSONObject(i);
                dateTime = dayTime.setJulianDay(julianStartDay+i);

                pressure = dayForecast.getDouble(Utility.OWM_PRESSURE);
                humidity = dayForecast.getInt(Utility.OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(Utility.OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(Utility.OWM_WIND_DIRECTION);

                JSONObject weatherObject = dayForecast.getJSONArray(Utility.OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(Utility.OWM_DESCRIPTION);
                weatherId = weatherObject.getString(Utility.OWM_WEATHER_ID);

                JSONObject temperatureObject = dayForecast.getJSONObject(Utility.OWM_TEMPERATURE);
                high = temperatureObject.getDouble(Utility.OWM_MAX);
                low = temperatureObject.getDouble(Utility.OWM_MIN);

                ContentValues weatherValues = new ContentValues();

                weatherValues.put(Utility.WeatherEntry.COLUMN_LOC_KEY, locationId);
                weatherValues.put(Utility.WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(Utility.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(Utility.WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(Utility.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(Utility.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(Utility.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(Utility.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(Utility.WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(Utility.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                cVVector.add(weatherValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(Utility.WeatherEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private long addLocation(String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        Cursor locationCursor = getContext().getContentResolver().query(
                Utility.LocationEntry.CONTENT_URI,
                new String[]{Utility.LocationEntry._ID},
                Utility.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);

        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(Utility.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            ContentValues locationValues = new ContentValues();

            locationValues.put(Utility.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(Utility.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(Utility.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(Utility.LocationEntry.COLUMN_COORD_LONG, lon);

            Uri insertedUri = getContext().getContentResolver().insert(
                    Utility.LocationEntry.CONTENT_URI,
                    locationValues
            );

            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        return locationId;
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            } else {
                onAccountCreated(newAccount, context);
            }
        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        ForecastSyncAdapter.configurePeriodicSync(context, Utility.SYNC_INTERVAL, Utility.SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void notifyWeather() {
        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        long lastSync = prefs.getLong(lastNotificationKey, 0);

        long elapsedTime = System.currentTimeMillis() - lastSync;
        boolean updateNotification = elapsedTime >= Utility.NOTIFICATION_INTERVAL;

        if(prefs.getBoolean(context.getString(R.string.pref_show_notification_key), true)) {
            if (updateNotification) {
                String locationQuery = Utility.getPreferredLocation(context);
                Uri weatherUri = Utility.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());
                Cursor cursor = context.getContentResolver().query(weatherUri, Utility.NOTIFICATION_COLUMNS, null, null, null);

                if (cursor.moveToFirst()) {
                    String desc = cursor.getString(Utility.COL_NOTIFICATION_SHORT_DESC);
                    String maxTemp = Utility.formatTemperature(cursor.getDouble(Utility.COL_NOTIFICATION_MAX_TEMP), Utility.isMetric(getContext()));
                    String minTemp = Utility.formatTemperature(cursor.getDouble(Utility.COL_NOTIFICATION_MIN_TEMP), Utility.isMetric(getContext()));
                    String title = context.getString(R.string.app_name);
                    int weatherIcon = Utility.getIcon(cursor.getString(Utility.COL_NOTIFICATION_WEATHER_ID));

                    RemoteViews contentView = new RemoteViews(getContext().getPackageName(), R.layout.notification);
                    contentView.setImageViewResource(R.id.notification_icon, weatherIcon);
                    contentView.setTextViewText(R.id.notification_description, Utility.formatDescription(desc));
                    contentView.setTextViewText(R.id.notification_temperature_min, minTemp);
                    contentView.setTextViewText(R.id.notification_temperature_max, maxTemp);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext()).setSmallIcon(weatherIcon).setContent(contentView).setContentTitle(title);
                    Intent resultIntent = new Intent(getContext(), MainActivity.class);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setOngoing(true);
                    mNotificationManager.notify(Utility.NOTIFICATION_ID, mBuilder.build());
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.apply();
                }
            }
        } else {
            mNotificationManager.cancelAll();

            if(!updateNotification) {
                editor.putLong(lastNotificationKey, System.currentTimeMillis()-Utility.NOTIFICATION_INTERVAL);
                editor.apply();
            }
        }
    }
}
