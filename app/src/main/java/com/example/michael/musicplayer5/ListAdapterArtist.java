package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapterArtist extends ArrayAdapter<SongObject> {

    Context context;
    int layoutResourceId;
    ArrayList<SongObject> data = null;

    public ListAdapterArtist(Context context, int layoutResourceId, ArrayList<SongObject> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        // if (row == null) { // <- Disable recycle logic
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        SongHolder songHolder = new SongHolder();

        songHolder.artist = (TextView) row.findViewById(R.id.artist);


        //     row.setTag(holder); // <- Disable recycle logic
        // } else { // <- Disable recycle logic
        //     holder = (SongHolder) row.getTag(); // <- Disable recycle logic
        // } // <- Disable recycle logic

        SongObject songObject = data.get(position);

        SongHolder.artist.setText(songObject.artist);

        return row;
    }

    static class SongHolder {
        static TextView artist;
    }
}