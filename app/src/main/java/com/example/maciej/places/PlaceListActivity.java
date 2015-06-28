package com.example.maciej.places;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;


@SuppressWarnings("ALL")
public class PlaceListActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Location userLocation;
    private Double userLatitude;
    private Double userLongitude;
    private String URL;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private static RecyclerView mRecyclerView;
    private static PlaceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getLocation();
    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void getLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_list, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            userLatitude = userLocation.getLatitude();
            userLongitude = userLocation.getLongitude();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("debug", "Location services connected.");
        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        userLatitude = userLocation.getLatitude();
        userLongitude = userLocation.getLongitude();

        URL = Constants.PLACES_URL + "?location=" + userLatitude + "," + userLongitude + "&radius=" + 5000
                + "&key=" + Constants.API_KEY;

        if (isOnline()) {
            (new AsyncPlacesDownload()).execute();
        }
        else {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("debug", "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("debug", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    public Context getContext() {
        return this;
    }


    private class AsyncPlacesDownload extends AsyncTask<String, Void, List<Place>> {

        private NetworkProvider networkProvider;

        @Override
        protected void onPostExecute(List<Place> result) {
            super.onPostExecute(result);

            mAdapter = new PlaceAdapter(PlaceListActivity.this, result);
            mRecyclerView.setAdapter(mAdapter);

            ViewAnimator viewAnimator = (ViewAnimator) findViewById(R.id.animator);
            viewAnimator.setDisplayedChild(1);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            networkProvider = new NetworkProvider(URL, PlaceListActivity.this, userLocation);
        }

        @Override
        protected List<Place> doInBackground(String... params) {
            try {
                networkProvider.getPlaces();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return networkProvider.getPlaceList();
        }
    }

}
