package com.example.maciej.places;


import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkProvider {

    private String URL;
    private Location location;
    private List<Place> placeList;

    public NetworkProvider(String URL, Location location) {
        this.URL = URL;
        this.location = location;
        this.placeList = new ArrayList<>();
    }

    public void getPlaces() throws IOException, JSONException {
        String s = downloadFromUrl();
        JSONObject jsonRequest = new JSONObject(s);
        JSONArray jsonResults = jsonRequest.getJSONArray("results");
        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject jsonPlace = jsonResults.getJSONObject(i);
            String name = jsonPlace.getString("name");
            double lat = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            double lng = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng");


            String address;
            try {
                address = jsonPlace.getString("vicinity");
            } catch (JSONException e){
                address = null;
            }

            String photoReference;
            try {
                photoReference = jsonPlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            } catch (JSONException e){
                photoReference = null;
            }

            Place place = null;
            if (address != null && photoReference != null) {
                place = new Place(photoReference, name, lat, lng, address);
            }
            else if (address != null) {
                place = new Place(name, lat, lng, address);
            }

            if (place != null) {
                Location placeLocation = new Location("placeLocation");
                placeLocation.setLatitude(place.getLat());
                placeLocation.setLongitude(place.getLng());

                place.setDistance(location.distanceTo(placeLocation) / 1000); // change metres to km
                placeList.add(place);
            }
        }
        Collections.sort(placeList, new Comparator<Place>() {
            public int compare(Place p1, Place p2) {
                return p1.getDistance().compareTo(p2.getDistance());
            }
        });
    }

    private String downloadFromUrl() throws IOException {
        InputStream is = null;

        try {
            java.net.URL url = new java.net.URL(URL);
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

    public List<Place> getPlaceList() { return placeList; }
}
