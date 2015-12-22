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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by michael on 12/22/15.
 */
public class LastFm {

    public static RequestQueue mRequestQueue;
    Context ctx;
    Activity activity;
    ArrayList<AlbumObject> requestList;

    public LastFm(Context ctx, ArrayList<AlbumObject> requestList){
        this.ctx=ctx;
        this.activity = (Activity) ctx;
        this.requestList=requestList;
    }

    /** Make request **/
    public void makeRequest(){
        Log.v("TAG", "LastFm is making req");
        new Thread() {
            public void run() {
                for(int i=0;i<requestList.size();i++){
                    if(requestList.get(i).albumArtURI.equals("null")){
                        fireRequest(requestList.get(i));
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }.start();
    }

    /** Make a Json request **/
    public void fireRequest(final AlbumObject albumObject){

        String requestUrl = getUrl(albumObject.albumTitle);

        Itunes.getRequestQueue();

        GsonRequest<ItunesAlbumInfo> myReq = new GsonRequest<>(
                Request.Method.GET,
                requestUrl,
                ItunesAlbumInfo.class,
                null,
                createMyReqSuccessListener(requestUrl, albumObject),
                createMyReqErrorListener(requestUrl));
        mRequestQueue.add(myReq);
    }

    /** Get url method **/
    static public String getUrl(String album){

        String requestUrl;
        requestUrl  = "http://ws.audioscrobbler.com/2.0/?";
        requestUrl += "method=album.search&";
        requestUrl += "album=" + urlEncode(album) + "&";
        requestUrl += "api_key=" + "26980b71e5c813da2c7e0156afdddd4f" + "&";
        requestUrl += "format=json";

        return requestUrl;
    }

    /** Url Encode method **/
    static public String urlEncode(String str){
        String encStr = null;
        try {
            encStr = URLEncoder.encode(str, "UTF_8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encStr;
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
    private Response.Listener<ItunesAlbumInfo> createMyReqSuccessListener(final String jsonUrl, final AlbumObject albumObject) {
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
                if(null == imageUrl){
                    //amzn.makeRequest();
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
                    }, 0, 0, null, createMyReqErrorListener(imageUrl));

                    mRequestQueue.add(myImageReq);
                }
            }
        };
    }

}
