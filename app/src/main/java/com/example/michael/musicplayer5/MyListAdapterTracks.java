package com.example.michael.musicplayer5;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MyListAdapterTracks extends ArrayAdapter<SongObject> {

    Context context;
    int layoutResourceId;
    ArrayList<SongObject> songObjectList;

    public MyListAdapterTracks(Context context, int layoutResourceId, ArrayList<SongObject> songObjectList) {

        super(context, layoutResourceId, songObjectList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.songObjectList = songObjectList;
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
        SongObject songObject = songObjectList.get(position);


        ViewHolder viewHolder; // view lookup cache stored in tag

        // if an existing view is not being reused
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.list_view_item, parent, false);
            convertView.setTag(viewHolder);

        // if an existing view is being reused
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            //viewHolder.albumArt = null;
        }

        // Set view holder references

        //viewHolder.album = (TextView) convertView.findViewById(R.id.album);
        viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
        viewHolder.title = (TextView) convertView.findViewById(R.id.title);
        viewHolder.albumArt = (ImageView) convertView.findViewById(R.id.album_art);
        //viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);

        // Set values to referenced view objects

        //viewHolder.album.setText(songObject.album);
        viewHolder.artist.setText(songObject.artist);
        viewHolder.title.setText(songObject.songTitle);
        //viewHolder.duration.setText(FormatTime(songObject.duration));
        //viewHolder.albumArt.setImageBitmap(BitmapFactory.decodeFile(songObject.albumArtURI));

        // Set a temporary gray background to the image view

        //viewHolder.albumArt.setBackgroundResource(R.drawable.grayalbumart);

        // Load album art asynchronously for smoother scrolling experience

        //new ImageLoader(viewHolder.albumArt).execute(songObject.albumArtURI);

        Log.v("albumArtURI",String.valueOf(songObject.albumArtURI));
        Log.v("artist",String.valueOf(songObject.artist));


        /** SET ALBUM ART TO LIST ITEMS USING PICASSO IMAGE LIBRARY **/

        if(songObject.albumArtURI != null){

            File f = new File(songObject.albumArtURI);

            /** CODE WITHOUT CIRCULAR TRANSFORM
            Picasso.with(viewHolder.albumArt.getContext())
                    .load(f)
                    .placeholder(R.drawable.grayalbumart)
                    .into(viewHolder.albumArt);**/

            /** CODE WITH CIRCULAR TRANSFORM **/
            Picasso.with(viewHolder.albumArt.getContext())
                    .load(f)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.blackcircle)
                    .into(viewHolder.albumArt);
        } else {

            /** CODE WITHOUT CIRCULAR TRANSFORM
            Picasso.with(viewHolder.albumArt.getContext())
                    .load(songObject.albumArtURI)
                    .placeholder(R.drawable.grayalbumart)
                    .into(viewHolder.albumArt);**/

            /** CODE WITH CIRCULAR TRANSFORM **/

            Picasso.with(viewHolder.albumArt.getContext())
                    .load(songObject.albumArtURI)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.blackcircle)
                    .into(viewHolder.albumArt);
        }

        return convertView;
    }

    public String FormatTime(String duration){

        return String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(duration)),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(duration)) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(duration))));
         /*
         // song duration returned in ms is converted to hh:mm:ss format
         return DateUtils.formatElapsedTime(Long.parseLong(duration) / 1000);
        */
    }

    public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> mWeakImageView;

        public ImageLoader(ImageView im) {

            mWeakImageView = new WeakReference<>(im);
        }

        @Override
        protected Bitmap doInBackground(String... uri) {

            return BitmapFactory.decodeFile(uri[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            ImageView imageView = mWeakImageView.get();
            if (imageView != null) {
                // set the bitmap
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}


