package de.gregoryseibert.wetter.model;

import java.io.Serializable;

import de.gregoryseibert.wetter.R;
import de.gregoryseibert.wetter.helper.Utility;

/**
 * Created by Gregory Seibert on 10.10.2016.
 */

public class Forecast implements Serializable {
    private String icon, date, description, min, max, wind, pressure, humidity;
    private int windDeg;

    public Forecast(String icon, String date, String description, String min, String max, String wind, int windDeg, String pressure, String humidity) {
        this.icon = icon;
        this.date = date;
        this.description = description;
        this.min = min;
        this.max = max;
        this.wind = wind;
        this.windDeg = windDeg;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public Forecast(String icon, String description, String max) {
        this.icon = icon;
        this.description = description;
        this.max = max;
    }

    public int getIcon() {
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

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public String getWind() { return wind + Utility.UNIT_WIND; }

    public int getWindDirectionIconCode() {
        if(getWindDirection() != null) {
            return getWindDirection().getIcon();
        } else {
            return 0;
        }
    }

    public String getWindDirectionName() {
        if(getWindDirection() != null) {
            return getWindDirection().getDir();
        } else {
            return "";
        }
    }

    public Utility.WindDirection getWindDirection() {
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

    public String getPressure() {
        return pressure + Utility.UNIT_PRESSURE;
    }

    public String getHumidity() {
        return humidity + Utility.UNIT_HUMIDITY;
    }
}
