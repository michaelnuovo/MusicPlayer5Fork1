package com.example.michael.musicplayer5;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceHandler {

    SongInfo[] JSONObjectsList;
    String url = "https://itunes.apple.com/search?term=michael+jackson";
    // http://is4.mzstatic.com/image/thumb/Music6/v4/68/b5/27/68b5273f-7044-8dbb-4ad1-82473837a136/source/30x30bb.jpg

    Context ctx1;
    Context ctx2;
    ImageView iv;
    SongObject songObject;
    AnInterface anInterface;

    public ServiceHandler(Context ctx1, ImageView iv, AnInterface anInterface){

        this.ctx1 = ctx1;
        this.ctx2 = ctx2;
        this.iv = iv;
        this.songObject = songObject;
        this.anInterface = anInterface;
    }

    public void runVolley(){

        RequestQueue requestQueue = Volley.newRequestQueue(ctx1);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // If there is a response, do this
                if (response != null) {

                    // Get the number of JSON objects on the web-page
                    int resultCount = response.optInt("resultCount");

                    // If there is a JSON object on the web-page, do this
                    if (resultCount > 0) {

                        // Get a gson object
                        Gson gson = new Gson();

                        // Get a JSONArray from the results
                        JSONArray jsonArray = response.optJSONArray("results");

                        // If the array exists, do this
                        if (jsonArray != null) {

                            // Convert the JSONArray into a Java object array
                            JSONObjectsList = gson.fromJson(jsonArray.toString(), SongInfo[].class);



                            anInterface.myTask(JSONObjectsList[0].artworkUrl30, ctx1, iv);


                            /*
                            Picasso.with(ctx2)
                                    .load(String.valueOf(JSONObjectsList[0].artworkUrl30))
                                    .transform(new CircleTransform())
                                    .placeholder(R.drawable.blackcircle)
                                    .into(iv);
                            */
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);



    }

    public interface MyInterface {

        public void myTask (SongInfo[] songInfo);
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
