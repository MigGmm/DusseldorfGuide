package com.example.miguel.guiadusseldorf.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.miguel.guiadusseldorf.R;

/**
 * Fragment used for change user's password.
 */
public class EditPasswordFragment extends Fragment {

    public EditText etOldPassword;
    public EditText etNewPassword;

    public EditPasswordFragment(){}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_password, container, false);
    }

    private void loadViews() {
        etOldPassword = (EditText) getView().findViewById(R.id.oldPassEdit);
        etNewPassword = (EditText) getView().findViewById(R.id.newPassEdit);
    }

    public String getOldPasswordText() {
        return etOldPassword.getText().toString();
    }

    public String getNewPasswordText() {
        return etNewPassword.getText().toString();
    }
}
