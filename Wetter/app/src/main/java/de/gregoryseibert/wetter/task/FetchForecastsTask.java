package de.gregoryseibert.wetter.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.gregoryseibert.wetter.R;
import de.gregoryseibert.wetter.adapter.ForecastAdapter;
import de.gregoryseibert.wetter.helper.Utility;
import de.gregoryseibert.wetter.model.Forecast;

/**
 * Created by gs71756 on 10.10.2016.
 */

public class FetchForecastsTask extends AsyncTask<String, Void, Forecast[]> {
    private Activity activity;
    private ForecastAdapter forecastAdapter;
    private RelativeLayout progressBarHolder;
    private final String LOG_TAG = FetchForecastsTask.class.getSimpleName();


    public FetchForecastsTask(Activity activity, ForecastAdapter forecastAdapter, RelativeLayout progressBarHolder) {
        this.activity = activity;
        this.forecastAdapter = forecastAdapter;
        this.progressBarHolder = progressBarHolder;
    }

    private String getReadableDateString(long time){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat(Utility.FORMAT_DATE, Locale.GERMAN);
        return shortenedDateFormat.format(time);
    }

    private String formatTemp(double temp, String unitType) {
        if (unitType.equals(activity.getString(R.string.pref_units_imperial))) {
            temp = (temp * 1.8) + 32;
            return Math.round(temp) + Utility.UNIT_TEMPERATURE_I;
        } else {
            return Math.round(temp) + Utility.UNIT_TEMPERATURE_M;
        }
    }

    private Forecast[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(Utility.OWM_LIST);

        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        dayTime = new Time();

        Forecast[] forecasts = new Forecast[numDays];

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String unitType = sharedPrefs.getString(activity.getString(R.string.pref_units_key), activity.getString(R.string.pref_units_metric));

        for(int i = 0; i < weatherArray.length(); i++) {
            String day, description, wind, pressure, humidity, icon;
            int windDeg;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            long dateTime;

            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            humidity = dayForecast.getString(Utility.OWM_HUMIDITY);
            wind = dayForecast.getString(Utility.OWM_WIND);
            wind = ""+Math.round(Double.parseDouble(wind)*3.6);
            windDeg = dayForecast.getInt(Utility.OWM_WIND_DEG);
            pressure = dayForecast.getString(Utility.OWM_PRESSURE);
            pressure = ""+Math.round(Double.parseDouble(pressure));

            JSONObject weatherObject = dayForecast.getJSONArray(Utility.OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(Utility.OWM_DESCRIPTION);
            icon = weatherObject.getString(Utility.OWM_ICON);

            JSONObject temperatureObject = dayForecast.getJSONObject(Utility.OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(Utility.OWM_MAX);
            double low = temperatureObject.getDouble(Utility.OWM_MIN);

            forecasts[i] = new Forecast(icon, day, description, formatTemp(low, unitType), formatTemp(high, unitType), wind, windDeg, pressure, humidity);
        }
        return forecasts;
    }

    @Override
    protected Forecast[] doInBackground(String... params) {
        if(params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;



        try {
            Uri builtUri = Uri.parse(Utility.OWM_FORECAST_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(Utility.OWM_QUERY_PARAM, params[0])
                    .appendQueryParameter(Utility.OWM_FORMAT_PARAM, Utility.OWM_FORMAT)
                    .appendQueryParameter(Utility.OWM_UNIT_PARAM, Utility.OWM_UNIT)
                    .appendQueryParameter(Utility.OWM_DAYS_PARAM, Integer.toString(Utility.OWM_NUM_DAYS))
                    .appendQueryParameter(Utility.OWM_LANG_PARAM, Utility.OWM_LANG)
                    .appendQueryParameter(Utility.OWM_APPID_PARAM, Utility.OWM_APIKEY).build();
            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, url.toString());

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
        } catch(IOException e) {
            Log.e(LOG_TAG, "Networking error! ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Couldn't close stream!", e);
                }
            }
        }

        try {
            return getWeatherDataFromJson(forecastJsonStr, Utility.OWM_NUM_DAYS);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Forecast[] forecasts) {
        if(forecasts != null) {
            if(forecasts.length > 0) {
                for(Forecast curForecast : forecasts) {
                    forecastAdapter.add(curForecast);
                }
            } else {
                forecastAdapter.add(new Forecast("11d", "Es konnten keine Wetterdaten abgerufen werden. Kein Internetzugriff möglich oder der eingegebene Standort existiert nicht.", ":("));
            }
        } else {
            forecastAdapter.add(new Forecast("11d", "Es konnte keine Verbindung hergestellt werden.", ":("));
        }

        Toast.makeText(activity, "Die Wetterdaten wurden aktualisiert.", Toast.LENGTH_SHORT).show();

        if(progressBarHolder.getVisibility() == View.VISIBLE) {
            progressBarHolder.setVisibility(View.INVISIBLE);
        }
    }
}
