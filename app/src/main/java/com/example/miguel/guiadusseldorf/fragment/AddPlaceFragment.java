package com.example.miguel.guiadusseldorf.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.miguel.guiadusseldorf.R;
import com.example.miguel.guiadusseldorf.activity.LoginActivity;
import com.example.miguel.guiadusseldorf.service.IsOnline;
import com.example.miguel.guiadusseldorf.service.JSONRequest;
import com.example.miguel.guiadusseldorf.util.ConstantStorage;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * Fragment used for fill all information about a new place.
 */
public class AddPlaceFragment extends Fragment {

    private Location location;

    private ImageView ivPlace;
    private EditText etName;
    private EditText etStreet;
    private EditText etTown;
    private EditText etLatitude;
    private EditText etLongitude;
    private String locality;
    private String address;
    private Bitmap bm;
    private ProgressDialog pDialog;
    private JSONArray jSONArray;
    private boolean imageLoaded;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_place, container, false);
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void loadViews(){
        ivPlace = (ImageView) getView().findViewById(R.id.ivAddPlace);
        etName = (EditText) getView().findViewById(R.id.nameAddPlace);
        etStreet = (EditText) getView().findViewById(R.id.streetAddPlace);
        etTown = (EditText) getView().findViewById(R.id.townAddPlace);
        etLatitude = (EditText) getView().findViewById(R.id.latitudeAddPlace);
        etLongitude = (EditText) getView().findViewById(R.id.longitudeAddPlace);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setAddress(String locality, String address) {
        this.locality = locality;
        this.address = address;
    }

    public void printLocation() {
        etStreet.setText(address);
        etTown.setText(locality);
        etLatitude.setText(location.getLatitude() + "");
        etLongitude.setText(location.getLongitude() + "");
    }

    /**
     * This method load gallery intent for choice the place image.
     */
    public void loadImage() {
        if (!etName.getText().toString().equals("")) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, ConstantStorage.INTENT_GALLERY_CODE);
        } else Toast.makeText(getActivity(), getString(R.string.type_name_before), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantStorage.INTENT_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            Uri galleryPicture = data.getData();
            try {
                InputStream is = getActivity().getContentResolver().openInputStream(galleryPicture);
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                FileOutputStream out = new FileOutputStream(getActivity().getFilesDir() + etName.getText().toString() + ConstantStorage.IMAGES_FORMAT);
                bm.compress(Bitmap.CompressFormat.JPEG, 25, out);
                bm = BitmapFactory.decodeFile(getActivity().getFilesDir() + etName.getText().toString() + ConstantStorage.IMAGES_FORMAT);
                ivPlace.setImageBitmap(Bitmap.createScaledBitmap(bm, 100, 150, false));
                imageLoaded = true;
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(bm!=null) {
                bm.recycle();
                bm = null;
            }
        } else if (requestCode == ConstantStorage.INTENT_GALLERY_CODE && resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(getActivity(), getString(R.string.load_image_failed), Toast.LENGTH_LONG).show();
    }

    /**
     * If all fields are filled this method create and execute an UploadPlace instance.
     * @return
     */
    public boolean savePlace() {
        if (etName.getText().toString().equals("")
                || etTown.getText().toString().equals("")
                || etStreet.getText().toString().equals("")
                || etLongitude.getText().toString().equals("")
                || etLatitude.getText().toString().equals("")
                || !imageLoaded) {
            Toast.makeText(getActivity(), getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (IsOnline.isOnline(getActivity())) {
                new UploadPlace().execute();
                return true;
            } else return false;
        }
    }

    // The network operations cant be done in the main thread of the application, so we need these classes.

    /**
     * This class send the information of the place to the server.
      */
    private class UploadPlace extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getString(R.string.uploading_information));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer;
            urlServer = ConstantStorage.BASIC_URL + ConstantStorage.SUSER + "=" + LoginActivity.connectedUser.getName()
                    + "&" + ConstantStorage.SNAME + "=" + etName.getText().toString()
                    + "&" + ConstantStorage.STOWN + "=" + etTown.getText().toString()
                    + "&" + ConstantStorage.SSTREET + "=" + etStreet.getText().toString()
                    + "&" + ConstantStorage.LATITUDE + "=" + etLatitude.getText().toString()
                    + "&" + ConstantStorage.LONGITUDE + "=" + etLongitude.getText().toString();
            JSONRequest jSONRequest = new JSONRequest();
            jSONArray = jSONRequest.getDataJSON(urlServer);
            if (jSONArray != null) {
                return true;
            } else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (result) {
                String status = "";
                try {
                    status = jSONArray.getJSONObject(0).get(ConstantStorage.INSERT_STATUS).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("1")) {
                    new UploadPicture().execute();
                    Toast.makeText(getActivity(), getString(R.string.place_saved), Toast.LENGTH_LONG).show();
                } else if (status.equals("0")) Toast.makeText(getActivity(), getString(R.string.name_already_exist), Toast.LENGTH_LONG).show();
                else if (status.equals("nopremium")) Toast.makeText(getActivity(), getString(R.string.need_to_be_premium), Toast.LENGTH_LONG).show();
                else if (status.equals("exist")) Toast.makeText(getActivity(), getString(R.string.name_already_exist), Toast.LENGTH_LONG).show();
                else Toast.makeText(getActivity(), getString(R.string.saving_profile_problem), Toast.LENGTH_LONG).show();
            } else Toast.makeText(getActivity(), getString(R.string.saving_profile_problem), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method upload the picture of the place via FTP.
     */
    private class UploadPicture extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            FTPClient ftpClient = null;
            try {
                ftpClient = new FTPClient();
                ftpClient.connect(InetAddress.getByName(ConstantStorage.FTP_URL));
                if (ftpClient.login(ConstantStorage.FTP_USER, ConstantStorage.FTP_PASS)) {
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.cwd(ConstantStorage.IMAGE_PLACES_DIR);
                    FileInputStream in = new FileInputStream(new File(getActivity().getFilesDir() + etName.getText().toString() + ConstantStorage.IMAGES_FORMAT));
                    boolean result = ftpClient.storeFile(etName.getText().toString() + ConstantStorage.IMAGES_FORMAT, in);
                    in.close();
                    if (result)
                        Log.v("upload result", "succeeded");
                    ftpClient.logout();
                    ftpClient.disconnect();
                    getActivity().deleteFile(etName.getText().toString() + ConstantStorage.IMAGES_FORMAT);
                    imageLoaded = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    private void dismissProgressDialog() {
        pDialog.dismiss();
    }
}
