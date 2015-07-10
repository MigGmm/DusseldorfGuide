package com.example.miguel.guiadusseldorf.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.miguel.guiadusseldorf.R;
import com.example.miguel.guiadusseldorf.fragment.AccountFragment;
import com.example.miguel.guiadusseldorf.fragment.AddCommentFragment;
import com.example.miguel.guiadusseldorf.fragment.AddPlaceFragment;
import com.example.miguel.guiadusseldorf.fragment.EditPasswordFragment;
import com.example.miguel.guiadusseldorf.fragment.PlaceFragment;
import com.example.miguel.guiadusseldorf.fragment.PlacesFragment;
import com.example.miguel.guiadusseldorf.fragment.Preferences;
import com.example.miguel.guiadusseldorf.model.Place;
import com.example.miguel.guiadusseldorf.service.IsOnline;
import com.example.miguel.guiadusseldorf.service.JSONRequest;
import com.example.miguel.guiadusseldorf.util.ConstantStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * If the login is positive, then this activity is called.
 */
public class MapActivity extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    public static ArrayList<Place> allPlaces = new ArrayList<>();
    private Location currentPosition;
    private GoogleMap mMap;
    private JSONArray jSONArray;
    private FrameLayout content;
    private String oldPassword;
    private String newPassword;
    private EditPasswordFragment editPasswordFragment;
    private AddPlaceFragment addPlaceFragment;
    private String locality;
    private String address;
    public static Place place;
    private AddCommentFragment addCommentFragment;
    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        content = (FrameLayout) findViewById(R.id.content);
        getPosition();
        getMap();
        setMapType();
        if (IsOnline.isOnline(this)) {
            new DownloadPlaces().execute();
        }
        centerDevice();
        loadMapFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMapType();
    }

    @Override
    public void onBackPressed() {
        loadMapFragment();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        centerDevice();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    // Buttons listeners
    public void mapClicked(View v) {
        loadMapFragment();
    }

    public void placesButtonClicked(View v) {
        loadPlacesFragment();
    }

    public void accountButtonClicked(View v) {
        loadAccountFragment();
    }

    public void preferencesButtonClicked(View v) {
        loadSettingsFragment();
    }

    public void logoutClicked(View v) {
        finish();
    }

    public void editPasswordClicked(View v) {
        loadEditPasswordFragment();
    }

    public void savePasswordClicked(View v) {
        oldPassword = "";
        newPassword = "";
        try {
            oldPassword = editPasswordFragment.getOldPasswordText();
            newPassword = editPasswordFragment.getNewPasswordText();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.exchange_password_problem), Toast.LENGTH_LONG).show();
        }
        if (!oldPassword.equals("")) {
            if (!newPassword.equals("")) {
                new ChangePassword().execute();
            } else
                Toast.makeText(this, getString(R.string.type_new_password), Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, getString(R.string.type_old_password), Toast.LENGTH_LONG).show();

    }

    public void cancelPasswordClicked(View v) {
        loadAccountFragment();
    }

    public void addPlaceClicked(View v) {
        loadAddPlaceFragment();
    }

    public void loadImageAddPlaceClicked(View v) {
        if (addPlaceFragment != null) {
            addPlaceFragment.loadImage();
        }
    }

    public void savePlaceClicked(View v) {
        if (addPlaceFragment != null) {
            addPlaceFragment.savePlace();
        }
    }

    public void cancelAddPlaceClicked(View v) {
        loadPlacesFragment();
    }

    public void loadCurrentLocationClicked(View v) {
        getPosition();
        if (addPlaceFragment != null) {
            addPlaceFragment.setLocation(currentPosition);
            loadLocality();
            addPlaceFragment.setAddress(locality, address);
            addPlaceFragment.printLocation();
        }
    }

    public void loadPlaceClicked(Place place) {
        this.place = place;
        loadPlaceFragment();
    }

    public void sendCommentClicked(View v) {
        if (addCommentFragment != null) comment = addCommentFragment.getComment();
        new UploadComment().execute();
    }

    public void newCommentClicked(View v) {
        addCommentFragment = new AddCommentFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, addCommentFragment)
                .addToBackStack(null)
                .commit();
        content.setVisibility(View.VISIBLE);
    }

    // Methods for load the fragments
    private void loadPlaceFragment(){
        PlaceFragment placeFragment = new PlaceFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, placeFragment)
                .addToBackStack(null)
                .commit();
        content.setVisibility(View.VISIBLE);
    }

    private void loadMapFragment() {
        content.setVisibility(View.GONE);
        setMapType();
        printMarkers();
    }

    private void loadPlacesFragment() {
        if (IsOnline.isOnline(this)) {
            new DownloadPlaces().execute();
        }
        PlacesFragment placesFragment = new PlacesFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, placesFragment)
                .addToBackStack(null)
                .commit();
        content.setVisibility(View.VISIBLE);
    }

    private void loadAccountFragment() {
        AccountFragment accountFragment = new AccountFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, accountFragment)
                .addToBackStack(null)
                .commit();
        content.setVisibility(View.VISIBLE);
    }

    private void loadSettingsFragment() {
        Preferences preferences = new Preferences();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, preferences)
                .addToBackStack(null)
                .commit();
        content.setVisibility(View.VISIBLE);
    }

    private void loadEditPasswordFragment() {
        editPasswordFragment = new EditPasswordFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, editPasswordFragment)
                .addToBackStack(null)
                .commit();
        content.setVisibility(View.VISIBLE);
    }

    private void loadAddPlaceFragment() {
        addPlaceFragment = new AddPlaceFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, addPlaceFragment)
                .addToBackStack(null)
                .commit();
        content.setVisibility(View.VISIBLE);
    }

    /**
     * This method get the map if mMap is null.
     */
    private void getMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map)).getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setOnMapClickListener(this);
            mMap.setOnMapLongClickListener(this);
        }
    }

    /**
     * This method try to get the actual position of the device, if its not possible the mthod get the
     * last know position.
     */
    private void getPosition() {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                currentPosition = location;
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
        Boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locListener);
            if (currentPosition == null) {
                currentPosition = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } else {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, locListener);
            if (currentPosition == null) {
                currentPosition = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }

    /**
     * Get the name and address of the location of the device.
     */
    private void loadLocality() {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(currentPosition.getLatitude(), currentPosition.getLongitude(), 1);
        } catch (IOException e) {

        }
        if (addresses != null) {
            if (addresses.size() > 0) {
                locality = addresses.get(0).getLocality();
                address = addresses.get(0).getAddressLine(0);
            }
        } else {
            Toast.makeText(this, getString(R.string.getting_location_problem), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Center Google Map over the device.
     */
    private void centerDevice() {
        try {
            LatLng latLng = new LatLng(currentPosition.getLatitude(), currentPosition.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } catch (Exception e){
            Toast.makeText(this, getString(R.string.enable_position), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Filter the preferenes fot show the map type.
     */
    private void setMapType() {
        if (mMap != null) {
            SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String mapType = pref.getString(ConstantStorage.MAPTYPE, "0");
            if (mapType != null)
                switch (mapType) {
                    case "0":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case "1":
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case "2":
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case "3":
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
        }
    }

    /**
     * Print all marks of the places returned by the server.
     */
    private void printMarkers() {
        if (mMap != null) {
            mMap.clear();
            Float colour = getMarkerColour();
            for (int i = 0; i < allPlaces.size(); i++) {
                LatLng posicion = new LatLng(allPlaces.get(i).getLatitude(), allPlaces.get(i).getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(posicion).title(allPlaces.get(i).getName() + " - " + allPlaces.get(i).getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(colour)));
            }
        }
    }

    /**
     * Return the colour chosen from the preferences.
     * @return
     */
    private Float getMarkerColour() {
        if (mMap != null) {
            SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String markerColour = pref.getString(ConstantStorage.MARKER_COLOUR, "0");
            if (markerColour != null)
                switch (markerColour) {
                    case "0":
                        return BitmapDescriptorFactory.HUE_RED;
                    case "1":
                        return BitmapDescriptorFactory.HUE_YELLOW;
                    case "2":
                        break;
                    case "3":
                        break;
                }
        }
        return BitmapDescriptorFactory.HUE_CYAN;
    }

    // The network operations cant be done in the main thread of the application, so we need these classes.

    /**
     * Inner class used for download all places and put them into an ArrayList.
     */
    private class DownloadPlaces extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.getting_places));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer = ConstantStorage.BASIC_URL + ConstantStorage.CPLACES;
            JSONRequest jSONRequest = new JSONRequest();
            jSONArray = jSONRequest.getDataJSON(urlServer);
            if (jSONArray != null) {
                return true;
            } else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                loadPlaces();
                printMarkers();
            }
            progressDialog.dismiss();
        }

        private void loadPlaces() {
            allPlaces = new ArrayList<>();
            for (int i = 0; i < jSONArray.length(); i++) {
                Place place = new Place();
                try {
                    place.setId(jSONArray.getJSONObject(i).getInt(ConstantStorage.ID));
                    place.setName(jSONArray.getJSONObject(i).getString(ConstantStorage.NAME));
                    place.setTown(jSONArray.getJSONObject(i).getString(ConstantStorage.TOWN));
                    place.setAddress(jSONArray.getJSONObject(i).getString(ConstantStorage.STREET));
                    place.setManager(jSONArray.getJSONObject(i).getString(ConstantStorage.ADMINISTRATOR));
                    place.setLatitude(jSONArray.getJSONObject(i).getDouble(ConstantStorage.LATITUDE));
                    place.setLongitude(jSONArray.getJSONObject(i).getDouble(ConstantStorage.LONGITUDE));
                    allPlaces.add(place);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Inner class for change the user's password.
     */
    private class ChangePassword extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.changing_password));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer = ConstantStorage.BASIC_URL + ConstantStorage.CUSER + "=" + LoginActivity.connectedUser.getName()
                    + "&" + ConstantStorage.CPASSWORD + "=" + oldPassword
                    + "&" + ConstantStorage.CPASSWORD2 + "=" + newPassword;
            JSONRequest jSONRequest = new JSONRequest();
            jSONArray = jSONRequest.getDataJSON(urlServer);
            if (jSONArray != null) {
                return true;
            } else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                String status = "";
                try {
                    status = jSONArray.getJSONObject(0).get(ConstantStorage.CHANGE_PASSWORD_STATUS).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("1")) {
                    Toast.makeText(MapActivity.this, getString(R.string.password_changed), Toast.LENGTH_LONG).show();
                    loadAccountFragment();
                } else if (status.equals("0"))
                    Toast.makeText(MapActivity.this, getString(R.string.bad_old_password), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MapActivity.this, getString(R.string.exchange_password_problem), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(MapActivity.this, getString(R.string.exchange_password_problem), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    /**
     * Inner class for upload the user's comments.
     */
    private class UploadComment extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.uploading_comment));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer = ConstantStorage.BASIC_URL
                    + ConstantStorage.ACUSER + "=" + LoginActivity.connectedUser.getName()
                    + "&" + ConstantStorage.COM + "=" + comment
                    + "&" + ConstantStorage.PLACE + "=" + place.getId();
            JSONRequest jSONRequest = new JSONRequest();
            jSONArray = jSONRequest.getDataJSON(urlServer);
            if (jSONArray != null) {
                return true;
            } else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result) {
                String status = "";
                try {
                    status = jSONArray.getJSONObject(0).getString(ConstantStorage.INSERT_STATUS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("1")) {
                    Toast.makeText(MapActivity.this, getString(R.string.comment_sent), Toast.LENGTH_LONG).show();
                    loadPlaceFragment();
                } else if (status.equals("0"))
                    Toast.makeText(MapActivity.this, getString(R.string.cant_add_comment), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MapActivity.this, getString(R.string.sending_comment_problem), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(MapActivity.this, getString(R.string.sending_comment_problem), Toast.LENGTH_LONG).show();
        }
    }
}
