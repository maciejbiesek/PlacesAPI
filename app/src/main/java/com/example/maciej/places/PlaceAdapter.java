package com.example.maciej.places;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    private List<Place> places;
    private Context context;

    public PlaceAdapter(Context context) {
        this.context = context;
        this.places = new ArrayList<Place>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView placePhoto;
        public TextView placeName;
        public TextView placeAddress;
        public TextView placeDistance;


        public ViewHolder(View v) {
            super(v);
            placePhoto = (ImageView) v.findViewById(R.id.place_photo);
            placeName = (TextView) v.findViewById(R.id.place_name);
            placeAddress = (TextView) v.findViewById(R.id.place_address);
            placeDistance = (TextView) v.findViewById(R.id.place_distance);
        }
    }

    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.placeName.setText(place.getName());
        holder.placeAddress.setText(place.getAddress());

        double distance = Math.round(place.getDistance() * 100) / 100.0;
        holder.placeDistance.setText(String.valueOf(distance) + " km");
        Picasso.with(context).load(place.getPhotoUrl()).into(holder.placePhoto);

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void setPlaces(List<Place> placeList) {
        places.clear();
        places.addAll(placeList);
    }

}
