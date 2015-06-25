package com.example.maciej.places;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


public class PlaceListActivity extends ActionBarActivity {

    private Location userLocation;
    private Double userLatitude;
    private Double userLongitude;
    private final String API_KEY = "AIzaSyCeC2TE5yhBdOGz5dnu4cIg1Q-Wi-gT-30";
    private String PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        getLocation();
        Toast.makeText(this, "" + userLatitude + ", " + userLongitude, Toast.LENGTH_SHORT).show();

        PLACES_URL += "?location=" + userLatitude + "," + userLongitude + "&radius=" + 5000 + "&key=" + API_KEY;
        Log.i("debug", PLACES_URL);

        if (isOnline()) {
            (new AsyncPlacesDownload()).execute();
        }
        else {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        userLocation = locationManager.getLastKnownLocation(provider);

        if(userLocation != null)
        {
            userLongitude = userLocation.getLongitude();
            userLatitude = userLocation.getLatitude();
        }
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

    private String getFromUrl() throws IOException {
        InputStream is = null;

        try {
            java.net.URL url = new java.net.URL(PLACES_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            return readStream(is);
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readStream(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }


    private class AsyncPlacesDownload extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(PlaceListActivity.this, result, Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getFromUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
