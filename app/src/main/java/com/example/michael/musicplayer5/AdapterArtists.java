package com.example.michael.musicplayer5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class AdapterArtists extends ArrayAdapter<ArtistObject> {


    Context context;
    int layoutResourceId;
    ArrayList<ArtistObject> artistObjectList;

    public AdapterArtists(Context context, int layoutResourceId, ArrayList<ArtistObject> artistObjectList) {

        super(context, layoutResourceId, artistObjectList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.artistObjectList = artistObjectList;
    }

    static class ViewHolder {

        static ImageView albumArt;
        static TextView album;
        static TextView artist;
        static TextView title;
        //static TextView data;
        static TextView duration;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ArtistObject artistObject = artistObjectList.get(position);


        ViewHolder viewHolder; // view lookup cache stored in tag

        // if an existing view is not being reused
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_list_view_artists, parent, false);
            convertView.setTag(viewHolder);

            // if an existing view is being reused
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            //viewHolder.albumArt = null;
        }

        // Set view holder references

        //viewHolder.album = (TextView) convertView.findViewById(R.id.album);
        viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
        //viewHolder.title = (TextView) convertView.findViewById(R.id.title);
        //viewHolder.albumArt = (ImageView) convertView.findViewById(R.id.album_art);
        //viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);

        // Set values to referenced view objects

        //viewHolder.album.setText(songObject.album);
        viewHolder.artist.setText(artistObject.artistName);
        //viewHolder.title.setText(songObject.title);
        //viewHolder.duration.setText(FormatTime(songObject.duration));
        //viewHolder.albumArt.setImageBitmap(BitmapFactory.decodeFile(songObject.albumArtURI));

        // Set a temporary gray background to the image view

        //viewHolder.albumArt.setBackgroundResource(R.drawable.grayalbumart);

        // Load album art asynchronously for smoother scrolling experience

        //new ImageLoader(viewHolder.albumArt).execute(songObject.albumArtURI);

        return convertView;
    }
}
