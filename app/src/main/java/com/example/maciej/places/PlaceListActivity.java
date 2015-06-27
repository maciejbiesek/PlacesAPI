package com.example.maciej.places;

import android.content.Context;
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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;


@SuppressWarnings("ALL")
public class PlaceListActivity extends ActionBarActivity {

    private Location userLocation;
    private Double userLatitude;
    private Double userLongitude;
    private String URL;

    private RecyclerView mRecyclerView;
    private PlaceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PlaceAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        getLocation();

        URL = Constants.PLACES_URL + "?location=" + userLatitude + "," + userLongitude + "&radius=" + 1000
                + "&key=" + Constants.API_KEY;

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




    private class AsyncPlacesDownload extends AsyncTask<String, Void, List<Place>> {

        private NetworkProvider networkProvider;

        @Override
        protected void onPostExecute(List<Place> result) {
            super.onPostExecute(result);
            mAdapter.setPlaces(result);
            mAdapter.notifyDataSetChanged();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            networkProvider = new NetworkProvider(URL, userLocation);
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
