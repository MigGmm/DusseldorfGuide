package com.example.miguel.guiadusseldorf.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.miguel.guiadusseldorf.R;
import com.example.miguel.guiadusseldorf.activity.MapActivity;
import com.example.miguel.guiadusseldorf.adapter.PlacesAdapter;
import com.example.miguel.guiadusseldorf.model.Place;

/**
 * Fragment with list view for show all places.
 */
public class PlacesFragment extends ListFragment {

    public PlacesFragment(){}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new PlacesAdapter(getActivity(), MapActivity.allPlaces));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Place place = (Place) l.getItemAtPosition(position);
        ((MapActivity) getActivity()).loadPlaceClicked(place);
    }
}
