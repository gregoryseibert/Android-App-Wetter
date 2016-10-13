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

import de.gregoryseibert.wetter.helper.Utility;
import de.gregoryseibert.wetter.model.Forecast;
import de.gregoryseibert.wetter.R;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DETAIL_LOADER = 0;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private String forecastStr;
    private ImageView mIconView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
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
        mDateView = (TextView) findViewById(R.id.list_item_date_textview);
        mDescriptionView = (TextView) findViewById(R.id.list_item_forecast_textview);
        mHighTempView = (TextView) findViewById(R.id.list_item_high_textview);
        mLowTempView = (TextView) findViewById(R.id.list_item_low_textview);
        mHumidityView = (TextView) findViewById(R.id.list_item_humidity_textview);
        mWindView = (TextView) findViewById(R.id.list_item_wind_textview);
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

            // Read date from cursor and update views for day of week and date
            long date = data.getLong(Utility.COL_DETAIL_WEATHER_DATE);
            String dateText = Utility.getDateStringComplete(date).replace(" -", "\n");
            mDateView.setText(dateText);

            // Read description from cursor and update view
            String description = data.getString(Utility.COL_DETAIL_WEATHER_DESC);
            mDescriptionView.setText(description);

            // Read high temperature from cursor and update view
            boolean isMetric = Utility.isMetric(this);

            double high = data.getDouble(Utility.COL_DETAIL_WEATHER_MAX_TEMP);
            String highString = Utility.formatTemperature(high, isMetric);
            mHighTempView.setText(highString);

            // Read low temperature from cursor and update view
            double low = data.getDouble(Utility.COL_DETAIL_WEATHER_MIN_TEMP);
            String lowString = Utility.formatTemperature(low, isMetric);
            mLowTempView.setText(lowString);

            // Read humidity from cursor and update view
            float humidity = data.getFloat(Utility.COL_DETAIL_WEATHER_HUMIDITY);
            mHumidityView.setText(Math.round(humidity) + "%");

            // Read wind speed and direction from cursor and update view
            float windSpeedStr = data.getFloat(Utility.COL_DETAIL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(Utility.COL_DETAIL_WEATHER_DEGREES);
            mWindView.setText(Utility.getFormattedWind(this, windSpeedStr, windDirStr));

            // Read pressure from cursor and update view
            float pressure = data.getFloat(Utility.COL_DETAIL_WEATHER_PRESSURE);
            mPressureView.setText(Math.round(pressure) + " hPa");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
