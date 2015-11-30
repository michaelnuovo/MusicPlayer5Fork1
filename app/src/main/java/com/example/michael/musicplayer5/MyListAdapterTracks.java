package com.example.michael.musicplayer5;


import android.app.Activity;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyListAdapterTracks extends ArrayAdapter<SongObject>  {

    Context context;
    int layoutResourceId;
    ArrayList<SongObject> songObjectList;
    Activity activity;

    public static RequestQueue mRequestQueue;

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.getAppContext());
        }
        return mRequestQueue;
    }

    public MyListAdapterTracks(Context context, int layoutResourceId, ArrayList<SongObject> songObjectList, Activity activity) {

        super(context, layoutResourceId, songObjectList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.songObjectList = songObjectList;
        this.activity = activity;
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

            convertView = inflater.inflate(R.layout.item_list_view, parent, false);
            convertView.setTag(viewHolder);

            // if an existing view is being reused
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            //viewHolder.albumArt = null; // Prevents recycling from overlapping list item contents
        }



        // Set view holder references

        //viewHolder.album = (TextView) convertView.findViewById(R.id.album);
        viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
        viewHolder.title = (TextView) convertView.findViewById(R.id.title);
        viewHolder.albumArt = (ImageView) convertView.findViewById(R.id.album_art);
        //viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);

        // Reset the image so we don't get overlap from recycling
        viewHolder.albumArt.setBackgroundResource(R.drawable.blackcircle);

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

        //Log.v("albumArtURI",String.valueOf(songObject.albumArtURI));
        //Log.v("artist",String.valueOf(songObject.artist));



        // If the album art uri exists, then use it
        //if(songObject.albumArtURI != null){

            // Picasso doesn't process URIs as strings, but as files
        if(songObject.albumArtURI != null){
            File f = new File(songObject.albumArtURI);

            Picasso.with(viewHolder.albumArt.getContext())
                    .load(f)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.blackcircle)
                    .into(viewHolder.albumArt);
        } else {

            Picasso.with(viewHolder.albumArt.getContext())
                    .load(R.drawable.blackcircle)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.blackcircle)
                    .into(viewHolder.albumArt);

        }




        // otherwise check if the URL exists and use that
        //} //else if (songObject.albumURL != null) {

           // String URL = songObject.albumURL;

          //  Picasso.with(viewHolder.albumArt.getContext())
          //          .load(URL)
          //          .transform(new CircleTransform())
           //         .placeholder(R.drawable.blackcircle)
          //          .into(viewHolder.albumArt);

        // otherwise download the image url and use that


            //VolleyClass vc = new VolleyClass(position, activity);
            //vc.runVolley();

            String JSONURL = "https://itunes.apple.com/search?term=michael+jackson";

            //RequestQueue queue = Volley.newRequestQueue(MainActivity.getAppContext());
            getRequestQueue();

            GsonRequest<SongInfo> myReq = new GsonRequest<SongInfo>(
                    Request.Method.GET,
                    JSONURL,
                    SongInfo.class,
                    null,
                    createMyReqSuccessListener(),
                    createMyReqErrorListener());

        mRequestQueue.add(myReq);
            Log.v("TAG", "Here");
/*
            new Thread() {
                public void run() {




                }
            }.start();*/


        return convertView;
    }

    public class MyClass {

    }

    private Response.Listener<SongInfo> createMyReqSuccessListener() {
        return new Response.Listener<SongInfo>() {
            @Override
            public void onResponse(SongInfo response) {
                // Do whatever you want to do with response;
                // Like response.tags.getListing_count(); etc. etc.

                Log.v("TAG", "This is the value of the string"+String.valueOf(response.artworkUrl30));
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
            }
        };
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

/** CODE WITHOUT CIRCULAR TRANSFORM
 Picasso.with(viewHolder.albumArt.getContext())
 .load(f)
 .placeholder(R.drawable.grayalbumart)
 .into(viewHolder.albumArt);**/

/** CODE WITH CIRCULAR TRANSFORM **/

/** CODE WITHOUT CIRCULAR TRANSFORM
 Picasso.with(viewHolder.albumArt.getContext())
 .load(songObject.albumArtURI)
 .placeholder(R.drawable.grayalbumart)
 .into(viewHolder.albumArt);**/

/** CODE WITH CIRCULAR TRANSFORM

 Picasso.with(viewHolder.albumArt.getContext())
 .load(songObject.albumArtURI)
 .transform(new CircleTransform())
 .placeholder(R.drawable.blackcircle)
 .into(viewHolder.albumArt); **/