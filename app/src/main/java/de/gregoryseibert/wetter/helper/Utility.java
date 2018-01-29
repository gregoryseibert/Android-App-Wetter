package de.gregoryseibert.wetter.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.text.SimpleDateFormat;

import de.gregoryseibert.wetter.R;

/**
 * Created by Gregory Seibert on 12.10.2016.
 */

public class Utility {
    public static final int SYNC_INTERVAL = 60 * 60; //one hour
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    //date replacement strings
    public final static String TODAY_TEXT = "Heute";
    public final static String TOMORROW_TEXT = "Morgen";

    //openweathermap related constants and variables
    public final static String OWM_APPID_PARAM = "APPID";
    public final static String OWM_APIKEY = "ec9b11616de5bdf30338ff74597d9a3a";
    public final static String OWM_FORMAT = "json";
    public final static String OWM_UNIT = "metric";
    public final static String OWM_REQUEST_METHOD = "GET";
    public final static String OWM_FORECAST_BASE_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?";
    public final static String OWM_QUERY_PARAM = "q";
    public final static String OWM_FORMAT_PARAM = "mode";
    public final static String OWM_UNIT_PARAM = "units";
    public final static String OWM_DAYS_PARAM = "cnt";
    public final static String OWM_CITY = "city";
    public final static String OWM_CITY_NAME = "name";
    public final static String OWM_LANG_PARAM = "lang";
    public final static String OWM_LANG = "de";
    public final static int OWM_NUM_DAYS = 7;
    public final static String OWM_COORD = "coord";
    public final static String OWM_LATITUDE = "lat";
    public final static String OWM_LONGITUDE = "lon";
    public final static String OWM_LIST = "list";
    public final static String OWM_PRESSURE = "pressure";
    public final static String OWM_HUMIDITY = "humidity";
    public final static String OWM_WINDSPEED = "speed";
    public final static String OWM_WIND_DIRECTION = "deg";
    public final static String OWM_TEMPERATURE = "temp";
    public final static String OWM_MAX = "max";
    public final static String OWM_MIN = "min";
    public final static String OWM_WEATHER = "weather";
    public final static String OWM_DESCRIPTION = "description";
    public final static String OWM_WEATHER_ID = "icon";



    //format for several actions
    public final static String FORMAT_DATE = "EEEE - dd.M.y";

    //units for the weather data
    public final static String UNIT_TEMPERATURE_M = " °C";
    public final static String UNIT_TEMPERATURE_I = "F";
    public final static String UNIT_WIND = " km/h";
    public final static String UNIT_PRESSURE = " hPa";
    public final static String UNIT_HUMIDITY = "%";

    //wind direction data holders
    public final static WindDirection WIND_DIR_1 = new WindDirection("Nord", R.string.wi_direction_down);
    public final static WindDirection WIND_DIR_2 = new WindDirection("Nord-Ost", R.string.wi_direction_down_left);
    public final static WindDirection WIND_DIR_3 = new WindDirection("Ost", R.string.wi_direction_left);
    public final static WindDirection WIND_DIR_4 = new WindDirection("Süd-Ost", R.string.wi_direction_up_left);
    public final static WindDirection WIND_DIR_5 = new WindDirection("Süd",  R.string.wi_direction_up);
    public final static WindDirection WIND_DIR_6 = new WindDirection("Süd-West", R.string.wi_direction_up_right);
    public final static WindDirection WIND_DIR_7 = new WindDirection("West", R.string.wi_direction_right);
    public final static WindDirection WIND_DIR_8 = new WindDirection("Nord-West", R.string.wi_direction_down_right);

    public final static int ALTERNATED_ROW_BACKGROUND_COLOR = Color.argb(10, 255, 255, 255);
    public final static int FLOATING_ACTION_BUTTON_ANIMATION_DUR = 500;

    //DB Stuff
    public static final String CONTENT_AUTHORITY = "de.gregoryseibert.wetter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    public static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_COORD_LAT,
            LocationEntry.COLUMN_COORD_LONG
    };

    public static final int COL_FORECAST_WEATHER_ID = 0;
    public static final int COL_FORECAST_WEATHER_DATE = 1;
    public static final int COL_FORECAST_WEATHER_DESC = 2;
    public static final int COL_FORECAST_WEATHER_MAX_TEMP = 3;
    public static final int COL_FORECAST_WEATHER_MIN_TEMP = 4;
    public static final int COL_FORECAST_LOCATION_SETTING = 5;
    public static final int COL_FORECAST_WEATHER_CONDITION_ID = 6;
    public static final int COL_FORECAST_COORD_LAT = 7;
    public static final int COL_FORECAST_COORD_LONG = 8;

    public static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_LOCATION_SETTING
    };

    public static final int COL_DETAIL_WEATHER_ID = 0;
    public static final int COL_DETAIL_WEATHER_DATE = 1;
    public static final int COL_DETAIL_WEATHER_DESC = 2;
    public static final int COL_DETAIL_WEATHER_MAX_TEMP = 3;
    public static final int COL_DETAIL_WEATHER_MIN_TEMP = 4;
    public static final int COL_DETAIL_WEATHER_HUMIDITY = 5;
    public static final int COL_DETAIL_WEATHER_PRESSURE = 6;
    public static final int COL_DETAIL_WEATHER_WIND_SPEED = 7;
    public static final int COL_DETAIL_WEATHER_DEGREES = 8;
    public static final int COL_DETAIL_WEATHER_CONDITION_ID = 9;

    public static final String[] NOTIFICATION_COLUMNS = new String[] {
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_SHORT_DESC
    };

    public static final int COL_NOTIFICATION_WEATHER_ID = 0;
    public static final int COL_NOTIFICATION_MAX_TEMP = 1;
    public static final int COL_NOTIFICATION_MIN_TEMP = 2;
    public static final int COL_NOTIFICATION_SHORT_DESC = 3;

    public static final int VIEW_TYPE_COUNT = 2;
    public static final int VIEW_TYPE_TODAY = 0;
    public static final int VIEW_TYPE_FUTURE_DAY = 1;

    public static final long NOTIFICATION_INTERVAL = 1000 * 60 * 60; //1 hour
    public static final int NOTIFICATION_ID = 3003;


    public static final class LocationEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class WeatherEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }


    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if (!isMetric) {
            temp = 9*temperature/5+32;
            return String.format("%.0f%s", temp, UNIT_TEMPERATURE_I);
        } else {
            temp = temperature;
            return String.format("%.0f%s", temp, UNIT_TEMPERATURE_M);
        }
    }

    public static String getFormattedWind(Context context, float windSpeed) {
        if (Utility.isMetric(context)) {
            return String.format("%.0f km/h", windSpeed);
        } else {
            return String.format("%.0f km/h", .621371192237334f * windSpeed);
        }
    }

    public static int getWindDirectionIconCode(float windDeg) {
        if(getWindDirection(windDeg) != null) {
            return getWindDirection(windDeg).getIcon();
        } else {
            return 0;
        }
    }

    public static String getWindDirectionName(float windDeg) {
        if(getWindDirection(windDeg) != null) {
            return getWindDirection(windDeg).getDir();
        } else {
            return "";
        }
    }

    public static Utility.WindDirection getWindDirection(float windDeg) {
        if(windDeg > 337.5 && windDeg < 22.5) {         //n
            return Utility.WIND_DIR_1;
        } else if (windDeg > 22.5 && windDeg < 67.5) {    //ne
            return Utility.WIND_DIR_2;
        } else if (windDeg > 67.5 && windDeg < 112.5) {    //e
            return Utility.WIND_DIR_3;
        } else if (windDeg > 112.5 && windDeg < 157.5) {   //se
            return Utility.WIND_DIR_4;
        } else if (windDeg > 157.5 && windDeg < 202.5) {  //s
            return Utility.WIND_DIR_5;
        } else if (windDeg > 202.5 && windDeg < 247.5) {  //sw
            return Utility.WIND_DIR_6;
        } else if (windDeg > 247.5 && windDeg < 292.5) {  //w
            return Utility.WIND_DIR_7;
        } else if (windDeg > 292.5 && windDeg < 337.5) {  //nw
            return Utility.WIND_DIR_8;
        } else {
            return null;
        }
    }

    public static String getDateString(long dateInMillis) {
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        if (julianDay == currentJulianDay) {
            return Utility.TODAY_TEXT;
        } else if (julianDay == currentJulianDay + 1) {
            return Utility.TOMORROW_TEXT;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
            return dateFormat.format(dateInMillis);
        }
    }

    public static String getDateStringComplete(long dateInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
        return dateFormat.format(dateInMillis);
    }

    public static String formatDescription(String description) {
        return Character.toUpperCase(description.charAt(0)) + description.substring(1);
    }

    public static int getIcon(String icon) {
        if(icon.contains("01")) {
            return R.drawable.sunny;
        } else if(icon.contains("02")) {
            return R.drawable.mostlycloudy;
        } else if(icon.contains("03") || icon.contains("04")) {
            return R.drawable.cloudy;
        } else if(icon.contains("09")) {
            return R.drawable.drizzle;
        } else if(icon.contains("10")) {
            return R.drawable.slightdrizzle;
        } else if(icon.contains("11")) {
            return R.drawable.thunderstorms;
        } else if(icon.contains("13")) {
            return R.drawable.snow;
        } else if(icon.contains("50")) {
            return R.drawable.haze;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    public static class WindDirection {
        private String dir;
        private int icon;

        public WindDirection(String dir, int icon) {
            this.dir = dir;
            this.icon = icon;
        }

        public String getDir() {
            return dir;
        }

        public int getIcon() {
            return icon;
        }
    }

}
