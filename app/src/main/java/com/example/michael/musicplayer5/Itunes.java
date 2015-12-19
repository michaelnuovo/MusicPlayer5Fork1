package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

/**
 * This class allows the user to make a json request.
 * After the json request is made, and if the request is successful,
 * this class will parse the json request for an image url.
 * The parsed image url will be the first image url in the first json object of a json object array.
 * This class will then make an image request using the image url.
 * This class will then save that image to disk with another class.
 * This class will also update the filepath to the Android meta data using another class.
 * This class will also update the application adapters using another class notifying them of the underling file path change.
 */
public class Itunes {

    Long albumId;
    public static RequestQueue mRequestQueue;
    Context ctx;
    String albumTitle;
    String artist;
    Activity activity;
    String albumArtUri;
    SongObject songObject;

    /** Constructor **/
    public Itunes(SongObject songObject, String albumArtURI, Long albumId, Context ctx, String albumTitle, String artist, Activity activity)
    {
        this.songObject = songObject;
        this.albumArtUri = albumArtURI;
        this.albumId = albumId;
        this.ctx = ctx;
        this.albumTitle = albumTitle;
        this.artist = artist;
        this.activity = activity;
    }

    /** Make a Json request **/
    public void makeRequest(String jsonObjectArrayUrl){
        //String JsonUrl = "https://itunes.apple.com/search?term=michael+jackson&entity=album";
        getRequestQueue();
        GsonRequest<AlbumInfo> myReq = new GsonRequest<>(
                Request.Method.GET,
                jsonObjectArrayUrl,
                AlbumInfo.class,
                null,
                createMyReqSuccessListener(albumId, jsonObjectArrayUrl),
                createMyReqErrorListener(jsonObjectArrayUrl));
        mRequestQueue.add(myReq);
    }

    /** Static request queue (we don't want multiple request queue objects) **/
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.getAppContext());
        }
        return mRequestQueue;
    }

    /** custom error response listener **/
    private Response.ErrorListener createMyReqErrorListener(final String url) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
                Log.v("TAG","Bad url is: "+String.valueOf(url));
            }
        };
    }

    /** custom success response listener**/
    private Response.Listener<AlbumInfo> createMyReqSuccessListener(final Long albumId, final String jsonUrl) {
        return new Response.Listener<AlbumInfo>() {
            @Override

            /** Json request onResponse method **/
            public void onResponse(AlbumInfo response) {

                String imageUrl=null;

                Log.v("TAG","*************************************");
                Log.v("TAG","The image url is initialized to null ");
                Log.v("TAG","Does the image exist on iTunes? ");

                /** Get image url **/
                Log.v("TAG","(result count it "+response.resultCount+")");
                Log.v("TAG","The object url is "+jsonUrl);
                if(response.resultCount > 0){
                    //Log.v("TAG","response.results.size() is "+String.valueOf(response.results.size()));
                    for(int i=0;i<response.results.size();i++){
                        //Log.v("TAG","'i' is "+String.valueOf(i));
                        //Log.v("TAG","Collection name is "+response.results.get(i).collectionName);
                        if(null != response.results.get(i).collectionName &&                   // collection name must not be null
                                response.results.get(i).collectionName.contains(albumTitle)){  // for .contains() method not to throw an error
                            imageUrl = response.results.get(i).artworkUrl60;
                            Log.v("TAG","Yes it does exist on iTunes. ");
                            Log.v("TAG", "The image url is " + imageUrl);
                            break;
                        }
                        if(i==response.results.size()-1){
                            Log.v("TAG","album "+albumTitle+"not found");
                            Log.v("TAG","The object url is "+jsonUrl);
                        }
                    }
                    try {
                        imageUrl=imageUrl.replace("60x60bb","1300x1300bb"); // we need the higher resolution version
                    } catch (NullPointerException e) {
                        // do nothing
                    }
                } else {
                    Log.v("TAG","No it does not exist on iTunes. ");
                    Log.v("TAG","The object url is "+jsonUrl);
                    Log.v("TAG", "Does the image exist on Spotify? ");
                }

                /** Try Spotify if image url is null **/
                if(null == imageUrl){
                    Parse parse = new Parse();
                    Spotify spotify = new Spotify(songObject, albumId, ctx, albumTitle, artist, activity);
                    spotify.makeRequest(parse.spotifyUrl(artist,albumTitle));
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

                                    //Update uri path
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

                    mRequestQueue.add(myImageReq);
                }
            }
        };
    }
}
