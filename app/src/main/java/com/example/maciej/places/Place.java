package com.example.maciej.places;


public class Place {
    private String photoUrl;
    private String name;
    private double lat;
    private double lng;
    private String address;
    private double distance;

    public Place(String photoReference, String name, double lat, double lng, String address) {
        this.photoUrl = Constants.PHOTOS_URL + "?maxwidth=" + 400 + "&photoreference=" + photoReference
                + "&key=" + Constants.API_KEY;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public Place(String name, double lat, double lng, String address) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }


    public String getPhotoUrl() { return photoUrl; }
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getAddress() { return address; }
    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }
}
