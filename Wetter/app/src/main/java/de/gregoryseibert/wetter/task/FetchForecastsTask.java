package de.gregoryseibert.wetter.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

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

import de.gregoryseibert.wetter.helper.Utility;

/**
 * Created by gs71756 on 10.10.2016.
 */

public class FetchForecastsTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchForecastsTask.class.getSimpleName();
    private final Context mContext;
    private boolean DEBUG = true;

    public FetchForecastsTask(Context context) {
        mContext = context;
    }

    long addLocation(String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        Cursor locationCursor = mContext.getContentResolver().query(
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

            Uri insertedUri = mContext.getContentResolver().insert(
                    Utility.LocationEntry.CONTENT_URI,
                    locationValues
            );

            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        return locationId;
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
                long dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;
                double high;
                double low;
                String description;
                String weatherId;

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

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(Utility.WeatherEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String locationQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;

        try {
            Uri builtUri = Uri.parse(Utility.OWM_FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(Utility.OWM_QUERY_PARAM, params[0])
                    .appendQueryParameter(Utility.OWM_FORMAT_PARAM, Utility.OWM_FORMAT)
                    .appendQueryParameter(Utility.OWM_LANG_PARAM, Utility.OWM_LANG)
                    .appendQueryParameter(Utility.OWM_UNIT_PARAM, Utility.OWM_UNIT)
                    .appendQueryParameter(Utility.OWM_DAYS_PARAM, Integer.toString(Utility.OWM_NUM_DAYS))
                    .appendQueryParameter(Utility.OWM_APPID_PARAM, Utility.OWM_APIKEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(Utility.OWM_REQUEST_METHOD);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
            getWeatherDataFromJson(forecastJsonStr, locationQuery);
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
        return null;
    }
}
