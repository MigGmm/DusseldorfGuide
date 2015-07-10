package com.example.miguel.guiadusseldorf.fragment;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miguel.guiadusseldorf.R;
import com.example.miguel.guiadusseldorf.activity.LoginActivity;
import com.example.miguel.guiadusseldorf.adapter.CommentsAdapter;
import com.example.miguel.guiadusseldorf.model.Comment;
import com.example.miguel.guiadusseldorf.service.JSONRequest;
import com.example.miguel.guiadusseldorf.util.ConstantStorage;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Fragment used to show the connected user.
 */
public class AccountFragment extends ListFragment {

    private TextView tvName;
    private TextView tvMail;
    private TextView tvType;
    private ImageView ivAvatar;
    private Bitmap bm;
    private JSONArray jSONArray;
    private ArrayList<Comment> allComments = new ArrayList<>();

    public AccountFragment(){}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadViews();
        printView();
        new DownloadComments().execute();
        try {
            bm = BitmapFactory.decodeFile(getActivity().getFilesDir() + LoginActivity.connectedUser.getName() + ConstantStorage.IMAGES_FORMAT);
            ivAvatar.setImageBitmap(Bitmap.createScaledBitmap(bm, 130, 148, false));
            if(bm!=null) {
                bm.recycle();
                bm = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            new DownloadImage().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    private void loadViews() {
        ivAvatar = (ImageView) getView().findViewById(R.id.ivAvatarAccount);
        tvName = (TextView) getView().findViewById(R.id.tvNameAccount);
        tvMail = (TextView) getView().findViewById(R.id.tvMailAcount);
        tvType = (TextView) getView().findViewById(R.id.tvTypeAccount);
    }

    private void printView() {
        tvName.setText(LoginActivity.connectedUser.getName());
        tvMail.setText(LoginActivity.connectedUser.getMail());
        tvType.setText(LoginActivity.connectedUser.getType());
    }

    // The network operations cant be done in the main thread of the application, so we need these classes.

    /**
     * Download the user's avatar via FTP.
     */
    private class DownloadImage extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.getting_image));
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
                    FileOutputStream out = new FileOutputStream(new File(getActivity().getFilesDir() + LoginActivity.connectedUser.getName() + ConstantStorage.IMAGES_FORMAT));
                    boolean result = ftpClient.retrieveFile(LoginActivity.connectedUser.getName() + ConstantStorage.IMAGES_FORMAT, out);
                    bm = BitmapFactory.decodeFile(getActivity().getFilesDir() + LoginActivity.connectedUser.getName() + ConstantStorage.IMAGES_FORMAT);
                    if (result)
                        Log.v("upload result", "succeeded");
                    ftpClient.logout();
                    ftpClient.disconnect();
                    getActivity().deleteFile(LoginActivity.connectedUser.getName() + ConstantStorage.IMAGES_FORMAT);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                ivAvatar.setImageBitmap(Bitmap.createScaledBitmap(bm, 100, 150, false));
                if(bm!=null) {
                    bm.recycle();
                    bm = null;
                }
            }
            progressDialog.dismiss();
        }
    }

    /**
     * Download all the comments made by the user.
     */
    private class DownloadComments extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.getting_comments));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlServer = ConstantStorage.BASIC_URL + ConstantStorage.CCUSER + "=" + LoginActivity.connectedUser.getName();
            JSONRequest jSONRequest = new JSONRequest();
            jSONArray = jSONRequest.getDataJSON(urlServer);
            if (jSONArray != null) {
                return true;
            } else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                loadComments();
                printComments();
            }
            progressDialog.dismiss();
        }

        private void loadComments() {
            allComments = new ArrayList<>();
            for (int i = 0; i < jSONArray.length(); i++) {
                Comment comment = new Comment();
                try {
                    comment.setName(jSONArray.getJSONObject(i).getString(ConstantStorage.USER));
                    comment.setComment(jSONArray.getJSONObject(i).getString(ConstantStorage.MESSAGE));
                    comment.setDate(jSONArray.getJSONObject(i).getString(ConstantStorage.DATE));
                    allComments.add(comment);
                } catch (Exception ignored) {
                }
            }
        }

        private void printComments() {
            setListAdapter(new CommentsAdapter(getActivity(), allComments));
        }
    }
}
