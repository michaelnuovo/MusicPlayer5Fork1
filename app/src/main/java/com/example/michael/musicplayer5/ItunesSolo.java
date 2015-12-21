package com.example.michael.musicplayer5;

/**
 * Created by michael on 12/20/15.
 */

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

import java.util.ArrayList;

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
public class ItunesSolo {


    public static RequestQueue mRequestQueue;
    Context ctx;
    Activity activity;
    ArrayList<SongObject> requestList;

    public ItunesSolo(Context ctx, ArrayList<SongObject> requestList){

        this.ctx=ctx;
        this.activity = (Activity) ctx;
        this.requestList=requestList;
    }

    /** Make request **/
    public void makeRequest(){
        new Thread() {
            public void run() {
                for(int i=0;i<requestList.size();i++){
                    fireRequest(requestList.get(i));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /** Make a Json request **/
    public void fireRequest(final SongObject songObject){
        //String JsonUrl = "https://itunes.apple.com/search?term=michael+jackson&entity=album";
        String jsonObjectArrayUrl = getItunesRequestUrl(songObject.artist);

        getRequestQueue();
        GsonRequest<ItunesAlbumInfo> myReq = new GsonRequest<>(
                Request.Method.GET,
                jsonObjectArrayUrl,
                ItunesAlbumInfo.class,
                null,
                createMyReqSuccessListener(Long.parseLong(songObject.albumID), jsonObjectArrayUrl, songObject),
                createMyReqErrorListener(jsonObjectArrayUrl));
        mRequestQueue.add(myReq);
    }

    static public String getItunesRequestUrl(String artists){

        // our model url:
        // https://itunes.apple.com/search?term=keith+jarrett+and+charlie+haden&media=music

        String splitNameString = artists.replace(" ", "+");
        return "https://itunes.apple.com/search?term=" + splitNameString + "&media=music" + "&limit=200"; // limit is 200 per page
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
                Log.v("TAG", "Bad url is: " + String.valueOf(url));
            }
        };
    }

    /** custom success response listener**/
    private Response.Listener<ItunesAlbumInfo> createMyReqSuccessListener(final Long albumId, final String jsonUrl, final SongObject songObject) {
        return new Response.Listener<ItunesAlbumInfo>() {
            @Override

            /** Json request onResponse method **/
            public void onResponse(ItunesAlbumInfo response) {

                String imageUrl=null;

                if(response.resultCount > 0){
                    //Log.v("TAG","response.results.size() is "+String.valueOf(response.results.size()));
                    for(int i=0;i<response.results.size();i++){
                        Log.v("TAG","'i' is "+String.valueOf(i));
                        Log.v("TAG","Collection name is "+response.results.get(i).collectionName);
                        Log.v("TAG","songObject.albumTitle is "+songObject.albumTitle);
                        if(null != response.results.get(i).collectionName &&                   // collection name must not be null
                                response.results.get(i).collectionName.contains(songObject.albumTitle)){  // for .contains() method not to throw an error
                            imageUrl = response.results.get(i).artworkUrl60;
                            Log.v("TAG","Yes it does exist on iTunes. ");
                            Log.v("TAG", "The image url is " + imageUrl);
                            break;
                        }
                        if(i==response.results.size()-1){
                            Log.v("TAG","album not found: "+songObject.albumTitle);
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
                    //Parse parse = new Parse();
                    //Spotify spotify = new Spotify(songObject, albumId, ctx, albumTitle, artist, activity);
                    //spotify.makeRequest(parse.spotifyUrl(artist,albumTitle));
                }

                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

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
