package de.gregoryseibert.wetter.helper;

import android.graphics.Color;

import de.gregoryseibert.wetter.R;

/**
 * Created by gs71756 on 12.10.2016.
 */

public class Utility {
    //key to send a forecast object through an intent
    public final static String EXTRA_KEY = "Forecast";

    //date replacement strings
    public final static String TODAY_TEXT = "Heute";
    public final static String TOMORROW_TEXT = "Morgen";

    //openweathermap related constants and variables
    public final static String OWM_FORMAT = "json";
    public final static String OWM_UNIT = "metric";
    public final static int OWM_NUM_DAYS = 7;
    public final static String OWM_LANG = "de";
    public final static String OWM_REQUEST_METHOD = "GET";
    public final static String OWM_FORECAST_BASE_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?";
    public final static String OWM_QUERY_PARAM = "q";
    public final static String OWM_FORMAT_PARAM = "mode";
    public final static String OWM_UNIT_PARAM = "units";
    public final static String OWM_DAYS_PARAM = "cnt";
    public final static String OWM_LANG_PARAM = "lang";
    public final static String OWM_APPID_PARAM = "APPID";
    public final static String OWM_APIKEY = "ec9b11616de5bdf30338ff74597d9a3a";
    public final static String OWM_LIST = "list";
    public final static String OWM_WEATHER = "weather";
    public final static String OWM_TEMPERATURE = "temp";
    public final static String OWM_MAX = "max";
    public final static String OWM_MIN = "min";
    public final static String OWM_WIND = "speed";
    public final static String OWM_WIND_DEG= "deg";
    public final static String OWM_PRESSURE = "pressure";
    public final static String OWM_HUMIDITY = "humidity";
    public final static String OWM_DESCRIPTION = "description";
    public final static String OWM_ICON = "icon";

    //format for several actions
    public final static String FORMAT_DATE = "EEEE - dd.M.y";

    //units for the weather data
    public final static String UNIT_TEMPERATURE_M = " °C";
    public final static String UNIT_TEMPERATURE_I = "F";
    public final static String UNIT_WIND = " km/h";
    public final static String UNIT_PRESSURE = " hPa";
    public final static String UNIT_HUMIDITY = "%";

    public final static WindDirection WIND_DIR_1 = new WindDirection("Nord", R.string.wi_direction_down);
    public final static WindDirection WIND_DIR_2 = new WindDirection("Nord-Ost", R.string.wi_direction_down_left);
    public final static WindDirection WIND_DIR_3 = new WindDirection("Ost", R.string.wi_direction_left);
    public final static WindDirection WIND_DIR_4 = new WindDirection("Süd-Ost", R.string.wi_direction_up_left);
    public final static WindDirection WIND_DIR_5 = new WindDirection("Süd",  R.string.wi_direction_up);
    public final static WindDirection WIND_DIR_6 = new WindDirection("Süd-West", R.string.wi_direction_up_right);
    public final static WindDirection WIND_DIR_7 = new WindDirection("West", R.string.wi_direction_right);
    public final static WindDirection WIND_DIR_8 = new WindDirection("Nord-West", R.string.wi_direction_down_right);

    public final static int ALTERNATED_ROW_BACKGROUND_COLOR = Color.argb(10, 255, 255, 255);

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
