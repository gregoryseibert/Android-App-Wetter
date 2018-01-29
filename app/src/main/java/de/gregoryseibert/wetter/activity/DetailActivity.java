package de.gregoryseibert.wetter.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pwittchen.weathericonview.WeatherIconView;

import de.gregoryseibert.wetter.util.Utility;
import de.gregoryseibert.wetter.R;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DETAIL_LOADER = 0;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private String forecastStr;
    private ImageView mIconView;
    private WeatherIconView mWindIconView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mWindDirView;
    private TextView mPressureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = this.getIntent();
        if (intent != null) {
            forecastStr = intent.getDataString();
        }

        if (null != forecastStr) {
            ((TextView) findViewById(R.id.list_item_forecast_textview)).setText(forecastStr);
        }

        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        mIconView = (ImageView) findViewById(R.id.list_item_icon);
        mWindIconView = (WeatherIconView) findViewById(R.id.my_weather_icon_wind);
        mDateView = (TextView) findViewById(R.id.list_item_date_textview);
        mDescriptionView = (TextView) findViewById(R.id.list_item_forecast_textview);
        mHighTempView = (TextView) findViewById(R.id.list_item_high_textview);
        mLowTempView = (TextView) findViewById(R.id.list_item_low_textview);
        mHumidityView = (TextView) findViewById(R.id.list_item_humidity_textview);
        mWindView = (TextView) findViewById(R.id.list_item_wind_textview);
        mWindDirView = (TextView) findViewById(R.id.list_item_wind_dir_textview);
        mPressureView = (TextView) findViewById(R.id.list_item_pressure_textview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else  if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(
                this,
                intent.getData(),
                Utility.DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String weatherId = data.getString(Utility.COL_DETAIL_WEATHER_CONDITION_ID);
            mIconView.setImageResource(Utility.getIcon(weatherId));

            long date = data.getLong(Utility.COL_DETAIL_WEATHER_DATE);
            String dateText = Utility.getDateStringComplete(date).replace(" -", "\n");
            mDateView.setText(dateText);

            String description = data.getString(Utility.COL_DETAIL_WEATHER_DESC);
            mDescriptionView.setText(Utility.formatDescription(description));

            boolean isMetric = Utility.isMetric(this);

            double high = data.getDouble(Utility.COL_DETAIL_WEATHER_MAX_TEMP);
            String highString = Utility.formatTemperature(high, isMetric);
            mHighTempView.setText(highString);

            double low = data.getDouble(Utility.COL_DETAIL_WEATHER_MIN_TEMP);
            String lowString = Utility.formatTemperature(low, isMetric);
            mLowTempView.setText(lowString);

            float humidity = data.getFloat(Utility.COL_DETAIL_WEATHER_HUMIDITY);
            mHumidityView.setText(Math.round(humidity) + Utility.UNIT_HUMIDITY);

            float windSpeedStr = data.getFloat(Utility.COL_DETAIL_WEATHER_WIND_SPEED);
            mWindView.setText(Utility.getFormattedWind(this, windSpeedStr));

            float windDirStr = data.getFloat(Utility.COL_DETAIL_WEATHER_DEGREES);
            mWindDirView.setText(Utility.getWindDirectionName(windDirStr));
            mWindIconView.setIconResource(getResources().getString(Utility.getWindDirectionIconCode(windDirStr)));

            float pressure = data.getFloat(Utility.COL_DETAIL_WEATHER_PRESSURE);
            mPressureView.setText(Math.round(pressure) + Utility.UNIT_PRESSURE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
