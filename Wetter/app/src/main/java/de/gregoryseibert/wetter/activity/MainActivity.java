package de.gregoryseibert.wetter.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;

import de.gregoryseibert.wetter.R;
import de.gregoryseibert.wetter.adapter.ForecastAdapter;
import de.gregoryseibert.wetter.data.ForecastSyncAdapter;
import de.gregoryseibert.wetter.helper.Utility;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FORECAST_LOADER = 0;
    private Activity activity;
    private AnimationSet animSet;
    private FloatingActionButton floatingActionButton;
    private ForecastAdapter forecastAdapter;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            findViewById(R.id.toolbarTitle).setVisibility(View.VISIBLE);
        }

        activity = this;
        location = Utility.getPreferredLocation(this);

        RotateAnimation animRotate = new RotateAnimation(0.0f, -180.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(Utility.FLOATING_ACTION_BUTTON_ANIMATION_DUR);
        animRotate.setFillAfter(true);
        animSet = new AnimationSet(true);
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);
        animSet.addAnimation(animRotate);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.startAnimation(animSet);
                updateWeather();
            }
        });

        forecastAdapter = new ForecastAdapter(this, null, 0);

        ListView listView = (ListView) findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(activity);
                    Intent intent = new Intent(activity, DetailActivity.class).setData(Utility.WeatherEntry.buildWeatherLocationWithDate(locationSetting, cursor.getLong(Utility.COL_FORECAST_WEATHER_DATE)));
                    startActivity(intent);
                }
            }
        });

        getSupportLoaderManager().initLoader(FORECAST_LOADER, null, this);

        ForecastSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String newLocation = Utility.getPreferredLocation( this );

        if (newLocation != null && !newLocation.equals(location)) {
            onLocationChanged();
            location = newLocation;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        //String location = Utility.getPreferredLocation(getActivity());
        //new FetchWeatherTask(getActivity()).execute(location)
        ForecastSyncAdapter.syncImmediately(this);
    }

    private void onLocationChanged( ) {
        updateWeather();
        getSupportLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(this);

        String sortOrder = Utility.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = Utility.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        return new CursorLoader(this, weatherForLocationUri, Utility.FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        forecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        forecastAdapter.swapCursor(null);
    }
}
