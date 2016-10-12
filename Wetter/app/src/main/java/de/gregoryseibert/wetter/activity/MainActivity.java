package de.gregoryseibert.wetter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.gregoryseibert.wetter.helper.Utility;
import de.gregoryseibert.wetter.task.FetchForecastsTask;
import de.gregoryseibert.wetter.model.Forecast;
import de.gregoryseibert.wetter.adapter.ForecastAdapter;
import de.gregoryseibert.wetter.R;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private FloatingActionButton floatingActionButton;
    private TextView locationText;
    private ListView forecastListView;
    private RelativeLayout progressBarHolder;
    private ForecastAdapter forecastAdapter;
    private FetchForecastsTask fetchForecastsTask;
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

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        locationText = (TextView) findViewById(R.id.locationText);
        locationText.setText(location);
        locationText.setVisibility(View.VISIBLE);

        progressBarHolder = (RelativeLayout) findViewById(R.id.progressBarHolder);

        forecastAdapter = new ForecastAdapter(this, R.layout.list_item_forecast, R.layout.list_item_forecast_today, new ArrayList<Forecast>());
        forecastListView = (ListView) findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(forecastAdapter);
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Forecast forecast = forecastAdapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class).putExtra(Utility.EXTRA_KEY, forecast);
                startActivity(intent);
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWeather();
            }
        });

        updateWeather();
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
        progressBarHolder.setVisibility(View.VISIBLE);

        forecastAdapter.clear();

        String newLocation = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        if(!location.equals(newLocation)) {
            location = newLocation;
            locationText.setText(location);
        }

        fetchForecastsTask = new FetchForecastsTask(this, forecastAdapter, progressBarHolder);
        fetchForecastsTask.execute(location);
    }
}
