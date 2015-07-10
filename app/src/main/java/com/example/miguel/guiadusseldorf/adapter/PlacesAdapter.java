package com.example.miguel.guiadusseldorf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miguel.guiadusseldorf.R;
import com.example.miguel.guiadusseldorf.model.Place;
import com.example.miguel.guiadusseldorf.util.ConstantStorage;

import java.util.ArrayList;

/**
 * Adapter for show places into a list view.
 */
public class PlacesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Place> places = new ArrayList<>();

    public PlacesAdapter (Context context, ArrayList<Place> places) {
        this.context = context;
        this.places = places;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = LayoutInflater.from(context);
        if(convertView==null)
            view = inflater.inflate(R.layout.place_list_view_layout, null,
                    true);
        TextView name = (TextView) view.findViewById(R.id.tvNamePlacesAdapter);
        TextView town = (TextView) view.findViewById(R.id.tvTownPlacesAdapter);
        name.setText(ConstantStorage.PRESENTATION_STYLE + places.get(position).getName());
        town.setText(ConstantStorage.PRESENTATION_STYLE + places.get(position).getTown());
        return view;
    }
}
