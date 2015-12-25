package com.example.michael.musicplayer5;

import android.app.Activity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VolleyClass {

    String url = "https://itunes.apple.com/search?term=michael+jackson";
    public static RequestQueue requestQueue = null;
    int listIndex;
    ArrayList<SongObject> songObjectList = ActivityMain.mainList;
    AdapterTracks adapter = MyFragmentTracks.adapter;
    private Activity activity;

    public VolleyClass(int listIndex, Activity activity){

        this.listIndex = listIndex;
        this.activity = activity;

    }

    public static void getRequestQueue() {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ActivityMain.getAppContext());
        } else {
            //do nothing
        }
    }

    public void runVolley(){

        // Here I set up the request queue

        getRequestQueue();

        // Here I make an json object request 

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override

            // Here I use GSON in the onResponse method to parse the object(s)
            public void onResponse(final JSONObject response) {
                if (response != null) {
                    int resultCount = response.optInt("resultCount");
                    if (resultCount > 0) {

                        // GSON needs to run in a new thread or I get performance issues

                        new Thread() {
                            public void run() {

                                Gson gson = new Gson();
                                JSONArray jsonArray = response.optJSONArray("results");
                                if (jsonArray != null) {

                                    // Here I parse the JSON object(s) into their Java class

                                    SongInfo[] JSONObjectsList = gson.fromJson(jsonArray.toString(), SongInfo[].class);

                                    // So here I need to update the array list and the list adapter respectively

                                    //Log.v("TAG","Position is "+String.valueOf(listIndex));
                                    //Log.v("TAG", "URI is "+toString().valueOf(songObjectList.get(listIndex).albumArtURI));
                                    songObjectList.get(listIndex).albumURL = JSONObjectsList[0].artworkUrl30;
                                    //Log.v("TAG", "URI is "+toString().valueOf(songObjectList.get(listIndex).albumArtURI));
                                    //adapter.notifyDataSetChanged();
                                    //MyInterface mf;
                                    //mf.myTask();

                                    //MyFragmentTracks.task();
                                    //MainActivity.task();

                                    //MyFragmentTracks.run();


                                    //if (activity!=null) {
                                    activity.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                    //}

                                    /*
                                    Picasso.with(iv.getContext())
                                            .load(JSONObjectsList[0].artworkUrl30)
                                            .transform(new CircleTransform())
                                            .placeholder(R.drawable.blackcircle)
                                            .into(iv);
                                    */


                                }

                            }
                        }.start();

                        /**
                        Gson gson = new Gson();
                        JSONArray jsonArray = response.optJSONArray("results");
                        if (jsonArray != null) {

                            // Here I parse the JSON object(s) into their Java class 

                            SongInfo[] JSONObjectsList = gson.fromJson(jsonArray.toString(), SongInfo[].class);

                        }**/
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
            }
        });

        // Here I add the object request object to the queue

        requestQueue.add(jsonObjectRequest);

    }

    public class SongInfo {
        public String wrapperType;
        public String kind;
        public Integer artistId;
        public Integer collectionId;
        public Integer trackId;
        public String artistName;
        public String collectionName;
        public String trackName;
        public String collectionCensoredName;
        public String trackCensoredName;
        public String artistViewUrl;
        public String collectionViewUrl;
        public String trackViewUrl;
        public String previewUrl;
        public String artworkUrl30;
        public String artworkUrl60;
        public String artworkUrl100;
        public Float collectionPrice;
        public Float trackPrice;
        public String releaseDate;
        public String collectionExplicitness;
        public String trackExplicitness;
        public Integer discCount;
        public Integer discNumber;
        public Integer trackCount;
        public Integer trackNumber;
        public Integer trackTimeMillis;
        public String country;
        public String currency;
        public String primaryGenreName;
        public String radioStationUrl;
        public Boolean isStreamable;
    }
}
