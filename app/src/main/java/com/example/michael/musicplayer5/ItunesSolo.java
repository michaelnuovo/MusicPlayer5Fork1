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
 * The problem with iTunes api is
 * it only allows search and match,
 * but not look-up.
 */

public class ItunesSolo {

    public static RequestQueue mRequestQueue;
    Context ctx;
    Activity activity;
    ArrayList<AlbumObject> albumObjectList;

    public void useApi () {
        //use another api
        AmazonSolo amzn = new AmazonSolo(ctx, albumObjectList);
        //amzn.makeRequest();
    }

    //AmazonSolo amzn;

    public ItunesSolo(Context ctx, ArrayList<AlbumObject> albumObjectList){

        this.ctx=ctx;
        this.activity = (Activity) ctx;
        this.albumObjectList=albumObjectList;

        //amzn = new AmazonSolo(ctx, requestList);
    }


    /** Make request **/
    public void makeRequest(){
        Log.v("TAG","intunes is making req");
        new Thread() {
            public void run() {
                for(int i=0;i<albumObjectList.size();i++){
                    if(albumObjectList.get(i).albumArtURI.equals("null")){
                        fireRequest(albumObjectList.get(i));
                        /**
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }**/
                    }
                }
            }
        }.start();
    }

    /** Make a Json request **/
    public void fireRequest(final AlbumObject albumObject){
        //String JsonUrl = "https://itunes.apple.com/search?term=michael+jackson&entity=album";
        String jsonObjectArrayUrl = getItunesRequestUrl(albumObject.albumArtist);

        getRequestQueue();
        GsonRequest<ItunesAlbumInfo> myReq = new GsonRequest<>(
                Request.Method.GET,
                jsonObjectArrayUrl,
                ItunesAlbumInfo.class,
                null,
                createMyReqSuccessListener(jsonObjectArrayUrl, albumObject),
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
            mRequestQueue = Volley.newRequestQueue(ActivityMain.getAppContext());
        }
        return mRequestQueue;
    }

    /** custom error response listener **/
    private Response.ErrorListener createMyReqErrorListener(final String url) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TAG","Itunes VolleyError error : "+error);

                if(error == null || error.equals("null")){
                    useApi();
                }
            }
        };
    }

    /** custom success response listener**/
    private Response.Listener<ItunesAlbumInfo> createMyReqSuccessListener(final String jsonUrl, final AlbumObject albumObject) {
        return new Response.Listener<ItunesAlbumInfo>() {
            @Override

            /** Json request onResponse method **/
            public void onResponse(ItunesAlbumInfo response) {

                String imageUrl=null;

                if(response.resultCount > 0 && !response.equals("")){

                    //Log.v("TAG","response.results.size() is "+String.valueOf(response.results.size()));
                    for(int i=0;i<response.results.size();i++){
                        Log.v("TAG","'i' is "+String.valueOf(i));
                        Log.v("TAG","Collection name is "+response.results.get(i).collectionName);
                        Log.v("TAG","songObject.albumTitle is "+albumObject.albumTitle);
                        if(null != response.results.get(i).collectionName &&                   // collection name must not be null
                                response.results.get(i).collectionName.contains(albumObject.albumTitle)){  // for .contains() method not to throw an error
                            imageUrl = response.results.get(i).artworkUrl60;
                            Log.v("TAG","Yes it does exist on iTunes. ");
                            Log.v("TAG", "The image url is " + imageUrl);
                            break;
                        }
                        if(i==response.results.size()-1){
                            Log.v("TAG","album not found: "+albumObject.albumTitle);
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
                if(null == imageUrl || imageUrl.equals("")){

                    useApi(); //use a different api

                } else {

                    /**
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }**/

                    /** Make an image request **/
                    ImageRequest myImageReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                        @Override

                        /** Image request onResponse method **/
                        public void onResponse(final Bitmap response) {

                            if(response.getHeight() ==1 && response.getWidth() == 1){ //If there is no image, Volley return a 1x1 px black bitmap as default

                                useApi(); //use a different api

                            } else {

                                new Thread() {
                                    public void run() {

                                        // Save the bitmap to disk, return an image path
                                        SaveBitMapToDisk saveImage = new SaveBitMapToDisk();
                                        saveImage.SaveImage(response, "myalbumart");
                                        String imagePathData = saveImage.getImagePath1();

                                        // Update the image path to Android meta data
                                        MediaStoreInterface mediaStore = new MediaStoreInterface(ctx);
                                        mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(Long.valueOf(albumObject.albumId), imagePathData);

                                        //Update uri path
                                        albumObject.albumArtURI = imagePathData;

                                        //up uri paths of song objects
                                        for(int i=0;i<albumObject.songObjectList.size();i++){
                                            albumObject.songObjectList.get(i).albumArtURI = imagePathData;
                                        }

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
                        }
                    }, 0, 0, null, createMyReqErrorListener(imageUrl));

                    mRequestQueue.add(myImageReq);

                }
            }
        };
    }
}
