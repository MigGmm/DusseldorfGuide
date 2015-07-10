package com.example.miguel.guiadusseldorf.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.miguel.guiadusseldorf.R;
import com.example.miguel.guiadusseldorf.model.User;
import com.example.miguel.guiadusseldorf.service.IsOnline;
import com.example.miguel.guiadusseldorf.service.JSONRequest;
import com.example.miguel.guiadusseldorf.util.ConstantStorage;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * First activity being launched, in this activity the user can login or request SignUpActivity.
 */
public class LoginActivity extends Activity {

    public static User connectedUser = new User();

    private EditText etName;
    private EditText etPass;
    private JSONArray jSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadViews() {
        etName = (EditText) findViewById(R.id.nameEditLogin);
        etPass = (EditText) findViewById(R.id.passEditLogin);
    }

    /**
     * Called when connect button is pressed.
     * @param v
     */
    public void connectClicked(View v) {
        if (IsOnline.isOnline(this)) new RequestJSON().execute();
        else Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    /**
     * Called when Sign Up button is pressed.
     * @param v
     */
    public void signUpClicked(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    // The network operations cant be done in the main thread of the application, so we need these classes.

    /**
     * Inner class for check the user's data with the database of the server. The server return 1 if
     * the user exist and the password is correct or 0 if not.
     */
    private class RequestJSON extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.checking_credentials));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer = ConstantStorage.BASIC_URL
                    + ConstantStorage.USER
                    + "=" + etName.getText().toString()
                    + "&" + ConstantStorage.PASSWORD + "=" + etPass.getText().toString();
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
                    status = jSONArray.getJSONObject(0).get(ConstantStorage.LOG_STATUS).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals(ConstantStorage.STATUS_OK)) {
                    connectedUser.setName(etName.getText().toString());
                    new RequestUser().execute();
                } else if (status.equals(ConstantStorage.STATUS_REFUSS)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.bad_name_pass), Toast.LENGTH_LONG).show();
                } else Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect), Toast.LENGTH_LONG).show();
            } else Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    /**
     * Once the user is checked and positive, the app request all the information about the connected user.
     */
    private class RequestUser extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.getting_user));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer = ConstantStorage.BASIC_URL + ConstantStorage.CUSER + "=" + LoginActivity.connectedUser.getName();
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
                generateUser();
                launchMapsActivity();
            }
        }

        private void generateUser() {
            try {
                connectedUser.setName(jSONArray.getJSONObject(0).get(ConstantStorage.NAME).toString());
                connectedUser.setMail(jSONArray.getJSONObject(0).get(ConstantStorage.EMAIL).toString());
                connectedUser.setType(jSONArray.getJSONObject(0).get(ConstantStorage.USER_TYPE).toString());
                connectedUser.setAvatar(jSONArray.getJSONObject(0).getInt(ConstantStorage.AVATAR));
            } catch (Exception e) {}
        }
    }

    private void launchMapsActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
