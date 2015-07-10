package com.example.miguel.guiadusseldorf.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.miguel.guiadusseldorf.R;

/**
 * Fragment for add comments to a place.
 */
public class AddCommentFragment extends Fragment {

    public AddCommentFragment(){}
    private EditText comment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comment = (EditText) getView().findViewById(R.id.etCommentAddComment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_comment_fragment, container, false);
    }

    public String getComment() {
        return comment.getText().toString();
    }
}
