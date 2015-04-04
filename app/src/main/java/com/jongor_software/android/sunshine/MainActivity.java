package com.jongor_software.android.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jongor_software.android.sunshine.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "*** - onCreate");

        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp).  If the view is present, then the activity should be in
            // two-pane mode
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment forecastFragment = ((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initialiseSyncAdapter(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "*** - onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "*** - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "*** - onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "*** - onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "*** - onResume");

        String location = Utility.getPreferredLocation(this);

        // Update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment forecastFragment =
                    (ForecastFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_forecast);
            if (forecastFragment != null) {
                forecastFragment.onLocationChanged();
            }

            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (detailFragment != null) {
                detailFragment.onLocationChanged(location);
            }

            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);

        // Using Uri scheme for location found on a map as per Android developer site:
        // https://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            // TODO: Toast pop-up, disable option until next onCreate
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }
}
