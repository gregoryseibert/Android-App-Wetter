package de.gregoryseibert.wetter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.gregoryseibert.wetter.util.Utility;

/**
 * Created by Gregory Seibert on 13.10.2016.
 */

public class ForecastDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "weather.db";

    public ForecastDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + Utility.LocationEntry.TABLE_NAME + " (" +
                Utility.LocationEntry._ID + " INTEGER PRIMARY KEY," +
                Utility.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                Utility.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                Utility.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                Utility.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + Utility.WeatherEntry.TABLE_NAME + " (" +
                Utility.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Utility.WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                Utility.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                Utility.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                Utility.WeatherEntry.COLUMN_WEATHER_ID + " TEXT NOT NULL," +
                Utility.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                Utility.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +
                Utility.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                Utility.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                Utility.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                Utility.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +
                " FOREIGN KEY (" + Utility.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                Utility.LocationEntry.TABLE_NAME + " (" + Utility.LocationEntry._ID + "), " +
                " UNIQUE (" + Utility.WeatherEntry.COLUMN_DATE + ", " +
                Utility.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Utility.LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Utility.WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
