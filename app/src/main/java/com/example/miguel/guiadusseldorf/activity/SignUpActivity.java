package com.example.miguel.guiadusseldorf.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.miguel.guiadusseldorf.R;
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
 * This class is used for the user sign up.
 */
public class SignUpActivity extends Activity {

    private ProgressDialog pDialog;
    private Bitmap bm;
    private ImageView avatar;
    private boolean avatarChanged = false;
    private EditText etName;
    private EditText etPass;
    private EditText etMail;
    private JSONArray jSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        loadViews();
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void loadViews() {
        avatar = (ImageView) findViewById(R.id.ivAvatarSignUp);
        etName = (EditText) findViewById(R.id.etNameSignUp);
        etPass = (EditText) findViewById(R.id.etPassSignUp);
        etMail = (EditText) findViewById(R.id.etMailSignUp);
    }

    /**
     * Call an intent for pick a picture from the gallery.
     * @param v
     */
    public void uploadAvatarClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, ConstantStorage.INTENT_GALLERY_CODE);
    }

    /**
     * When save button is clicked the app check the different fields, if all is correct create an
     * UploadUser instance and execute it.
     * @param v
     */
    public void saveClicked(View v) {
        if (!etName.getText().toString().equals(""))
            if (!etPass.getText().toString().equals(""))
                if (!etMail.getText().toString().equals(""))
                    new UploadUser().execute();
                else Toast.makeText(this, getString(R.string.no_mail), Toast.LENGTH_LONG).show();
            else Toast.makeText(this, getString(R.string.no_pass), Toast.LENGTH_LONG).show();
        else Toast.makeText(this, getString(R.string.no_name), Toast.LENGTH_LONG).show();
    }

    /**
     * Filter the result from the gallery intent.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantStorage.INTENT_GALLERY_CODE && resultCode == RESULT_OK) {
            Uri galleryPicture = data.getData();
            try {
                InputStream is = getContentResolver().openInputStream(galleryPicture);
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                FileOutputStream out = new FileOutputStream(getFilesDir() + etName.getText().toString() + ConstantStorage.IMAGES_FORMAT);
                bm.compress(Bitmap.CompressFormat.JPEG, 25, out);
                bm = BitmapFactory.decodeFile(getFilesDir() + etName.getText().toString() + ConstantStorage.IMAGES_FORMAT);
                avatar.setImageBitmap(Bitmap.createScaledBitmap(bm, 100, 150, false));
                avatarChanged = true;
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(bm!=null) {
                bm.recycle();
                bm = null;
            }
        } else if (requestCode == ConstantStorage.INTENT_GALLERY_CODE && resultCode == RESULT_CANCELED)
            Toast.makeText(this, getString(R.string.load_image_failed), Toast.LENGTH_LONG).show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    // The network operations cant be done in the main thread of the application, so we need these classes.

    /**
     * Used for upload the filler user's information.
     */
    private class UploadUser extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getString(R.string.uploading_information));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer;
            if (avatarChanged) urlServer = ConstantStorage.BASIC_URL + ConstantStorage.SUUSER + "="
                    + etName.getText().toString() + "&" + ConstantStorage.SUPASSWORD + "=" + etPass.getText().toString()
                    + "&" + ConstantStorage.SUMAIL + "=" + etMail.getText().toString()
                    + "&" + ConstantStorage.USER_TYPE + "=" + ConstantStorage.FREE
                    + "&" + ConstantStorage.AVATAR + "=" + "1";
            else urlServer = ConstantStorage.BASIC_URL + ConstantStorage.SUUSER + "="
                    + etName.getText().toString() + "&" + ConstantStorage.SUPASSWORD + "=" + etPass.getText().toString()
                    + "&" + ConstantStorage.SUMAIL + "=" + etMail.getText().toString()
                    + "&" + ConstantStorage.USER_TYPE + "=" + ConstantStorage.FREE
                    + "&" + ConstantStorage.AVATAR + "=" + "0";
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
                    if (avatarChanged) new UploadPicture().execute();
                    Toast.makeText(SignUpActivity.this, getString(R.string.profile_saved), Toast.LENGTH_LONG).show();
                    finish();
                } else if (status.equals("0")) Toast.makeText(SignUpActivity.this, getString(R.string.name_already_exist), Toast.LENGTH_LONG).show();
            } else Toast.makeText(SignUpActivity.this, getString(R.string.saving_profile_problem), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Used for upload the selected picture, the app use FTP for upload the image.
     */
    private class UploadPicture extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.uploading_information));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FTPClient ftpClient = null;
            try {
                ftpClient = new FTPClient();
                ftpClient.connect(InetAddress.getByName(ConstantStorage.FTP_URL));
                if (ftpClient.login(ConstantStorage.FTP_USER, ConstantStorage.FTP_PASS)) {
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.cwd(ConstantStorage.AVATARS_DIR);
                    FileInputStream in = new FileInputStream(new File(getFilesDir() + etName.getText().toString() + ConstantStorage.IMAGES_FORMAT));
                    boolean result = ftpClient.storeFile(etName.getText().toString() + ConstantStorage.IMAGES_FORMAT, in);
                    in.close();
                    if (result)
                        Log.v("upload result", "succeeded");
                    ftpClient.logout();
                    ftpClient.disconnect();
                    deleteFile(etName.getText().toString() + ConstantStorage.IMAGES_FORMAT);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (SignUpActivity.this.isDestroyed()) {
                return;
            }
            dismissProgressDialog();
        }
    }
}
