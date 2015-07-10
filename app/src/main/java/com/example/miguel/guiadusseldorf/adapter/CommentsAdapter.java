package com.example.miguel.guiadusseldorf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miguel.guiadusseldorf.R;
import com.example.miguel.guiadusseldorf.model.Comment;
import com.example.miguel.guiadusseldorf.model.Place;
import com.example.miguel.guiadusseldorf.util.ConstantStorage;

import java.util.ArrayList;

/**
 * Adapter for show comments into a list view.
 */
public class CommentsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Comment> comments = new ArrayList<>();

    public CommentsAdapter (Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
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
            view = inflater.inflate(R.layout.comment_list_view_layout, null,
                    true);
        TextView name = (TextView) view.findViewById(R.id.userComment);
        TextView town = (TextView) view.findViewById(R.id.commentComment);
        name.setText(ConstantStorage.PRESENTATION_STYLE + comments.get(position).getName());
        town.setText(ConstantStorage.PRESENTATION_STYLE + comments.get(position).getComment());
        return view;
    }
}
