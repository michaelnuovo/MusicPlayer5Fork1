package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 12/15/15.
 */
public class Spotify {

    Long albumId;
    //private static RequestQueue mRequestQueue;
    Context ctx;
    String albumTitle;
    String artist;
    Activity activity;
    SongObject songObject;

    /** Constructor **/
    public Spotify(SongObject songObject, Long albumId, Context ctx, String albumTitle, String artist, Activity activity)
    {
        this.albumId = albumId;
        this.ctx = ctx;
        this.albumTitle = albumTitle;
        this.artist = artist;
        this.activity = activity;
        this.songObject = songObject;
    }

    /** Make a Json request **/
    public void makeRequest(String jsonObjectArrayUrl){
        //String JsonUrl = "https://itunes.apple.com/search?term=michael+jackson&entity=album";
        Itunes.getRequestQueue();
        GsonRequest<SpotifyAlbumInfo> myReq = new GsonRequest<>(
                Request.Method.GET,
                jsonObjectArrayUrl,
                SpotifyAlbumInfo.class,
                null,
                createMyReqSuccessListener(albumId, jsonObjectArrayUrl),
                createMyReqErrorListener(jsonObjectArrayUrl));
        Itunes.mRequestQueue.add(myReq);
    }

    /** Static request queue (we don't want multiple request queue objects)
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.getAppContext());
        }
        return mRequestQueue;
    }**/

    /** custom error response listener **/
    private Response.ErrorListener createMyReqErrorListener(final String url) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
                Log.v("TAG", "Bad url is: " + String.valueOf(url));
            }
        };
    }

    /** custom success response listener**/
    private Response.Listener<SpotifyAlbumInfo> createMyReqSuccessListener(final Long albumId, final String jsonUrl) {
        return new Response.Listener<SpotifyAlbumInfo>() {
            @Override

            /** Json request onResponse method **/
            public void onResponse(SpotifyAlbumInfo response) {

                String imageUrl=null;                             // initialize the image url we will parse to null

                /** Spotify **/
                Log.v("TAG","Number of items is "+String.valueOf(response.albums.items.size()));
                Log.v("TAG","The json url is "+jsonUrl);
                if(response.albums.items.size() == 0){
                    Log.v("TAG","No it does not exist on Spotify");
                    Log.v("TAG","Trying Amazon...");
                } else {
                    imageUrl = response.albums.items.get(0).images.get(0).url; // get the first album url
                    Log.v("TAG","Yes, it does exist on Spotify.");
                    Log.v("TAG","The image url is "+imageUrl);
                }

                /** Amazon **/
                if(null != imageUrl){

                    Log.v("TAG","Json not on spotify, try xml on to amazon");
                    Log.v("TAG","Json url is "+jsonUrl);
                    Log.v("TAG","Album is "+albumTitle);

                    //Get xml url
                    SignedRequestsHelper helper = new SignedRequestsHelper();
                    Map<String, String> values = new HashMap<String, String>();
                    values.put("Operation", "ItemSearch");
                    values.put("AssociateTag", "mytag-20");
                    values.put("SearchIndex", "Music");
                    //temp.put("Keywords","The Essence Of Charlie Parker");
                    values.put("Artist", "Bill Evans Trio");
                    values.put("Title", "Sunday At the Village Vanguard");
                    String url = helper.sign(values);

                    //Submit the xml url to get an xml string response
                    Amazon amazon = new Amazon(albumId, ctx, albumTitle, artist);
                    amazon.makeRequest(url);
                }

                /** Download image **/
                if(null != imageUrl){ // if imageUrl is not null, do this

                    /** Make an image request **/
                    ImageRequest myImageReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                        @Override

                        /** Image request onResponse method **/
                        public void onResponse(final Bitmap response) {

                            new Thread() {
                                public void run() {

                                    // Save the bitmap to disk, return an image path
                                    SaveBitMapToDisk saveImage = new SaveBitMapToDisk();
                                    saveImage.SaveImage(response, "myalbumart");
                                    String imagePathData = saveImage.getImagePath();

                                    // Update the image path to Android meta data
                                    MediaStoreInterface mediaStore = new MediaStoreInterface(ctx);
                                    mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(albumId, imagePathData);

                                    //update uri path
                                    songObject.albumArtURI = imagePathData;

                                    // Update all adapters (not yet implemented)
                                    Log.v("TAG","value of activity is "+activity);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            UpdateAdapters.getInstance().update();

                                        }
                                    });


                                    //SaveBitmapAndRecordUri sbmruri = new SaveBitmapAndRecordUri(response, url, getContext(), songObject.albumID);
                                    //sbmruri.run();

                                    /** MediaStoreInterface
                                     String folderName = "MusicPlayer5";
                                     MediaStoreInterface msi = new MediaStoreInterface(context);
                                     msi.clearFolder(folderName);
                                     msi.createFolder(folderName);
                                     msi.saveBitMapToFolderWithRandomNumericalName(folderName, response);
                                     String imagePath = msi.getLastImagePath();
                                     msi.updateAndroidWithImagePath(imagePath, songObject.albumID);
                                     msi.dumpCursor(songObject.albumID);**/

                                    // So here I need to update the albumART URIs by (a) writing to the meta data and (b) updating the current song object URI
                                    // that way the URI is there on application restart, but also now so that Picasso has an image source to adapt to the list

                                    //notifyDataSetChanged(); // Notify the this list adapter class that the underlying data has changed
                                }
                            }.start();
                        }
                    }, 0, 0, null, createMyReqErrorListener(imageUrl));

                    Itunes.mRequestQueue.add(myImageReq);
                }
            }
        };
    }
}
