package de.gregoryseibert.wetter.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pwittchen.weathericonview.WeatherIconView;

import de.gregoryseibert.wetter.helper.Utility;
import de.gregoryseibert.wetter.model.Forecast;
import de.gregoryseibert.wetter.R;

public class DetailActivity extends AppCompatActivity {

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

        Forecast forecast = (Forecast) getIntent().getExtras().getSerializable(Utility.EXTRA_KEY);
        if(forecast != null) {
            ((ImageView) findViewById(R.id.list_item_icon)).setImageDrawable(getResources().getDrawable(forecast.getIcon()));
            ((TextView) findViewById(R.id.list_item_forecast_textview)).setText(forecast.getDescription());
            ((TextView) findViewById(R.id.list_item_date_textview)).setText(forecast.getDate().replace("- ", "\n"));
            ((TextView) findViewById(R.id.list_item_low_textview)).setText(forecast.getMin());
            ((TextView) findViewById(R.id.list_item_high_textview)).setText(forecast.getMax());
            ((TextView) findViewById(R.id.list_item_wind_textview)).setText(forecast.getWind());
            ((WeatherIconView) findViewById(R.id.my_weather_icon_wind)).setIconResource(getString(forecast.getWindDirectionIconCode()));
            ((TextView) findViewById(R.id.list_item_pressure_textview)).setText(forecast.getPressure());
            ((TextView) findViewById(R.id.list_item_humidity_textview)).setText(forecast.getHumidity());
        }
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
}
