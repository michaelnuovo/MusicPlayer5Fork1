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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by michael on 12/23/15.
 */
public class MusicBrains {

    public void useApi () {

        //use another api
    }

    public static RequestQueue mRequestQueue;
    Context ctx;
    Activity activity;
    ArrayList<AlbumObject> albumObjectList;

    public MusicBrains(Context ctx, ArrayList<AlbumObject> albumObjectList){
        this.ctx=ctx;
        this.activity = (Activity) ctx;
        this.albumObjectList=albumObjectList;
    }

    /** Make request **/
    public void makeRequest(){
        Log.v("TAG", "LastFm is making req");

        printAlbums();

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

        Log.v("TAG", "LastFm is firing a req");
        Log.v("TAG", "album fired is fired is "+albumObject.albumTitle);


        String requestUrl = getUrl(albumObject.albumTitle, albumObject.albumArtist);

        ItunesSolo.getRequestQueue();

        GsonRequest<LastFmAlbumInfoPojo> myReq = new GsonRequest<>(
                Request.Method.GET,
                requestUrl,
                LastFmAlbumInfoPojo.class,
                null,
                createMyReqSuccessListener(requestUrl, albumObject),
                createMyReqErrorListener(requestUrl, albumObject));
        ItunesSolo.mRequestQueue.add(myReq);
    }

    /** Print albums in albums list**/
    public void printAlbums(){
        for(int i=0;i<albumObjectList.size();i++){
            Log.v("TAG","albumObjectList.get(i).albumTitle is "+albumObjectList.get(i).albumTitle);
            Log.v("TAG","albumObjectList.get(i).albumTitle uri is "+albumObjectList.get(i).albumArtURI);
        }
    }

    /** Get url method **/
    static public String getUrl(String album, String artist){

        String requestUrl;

        requestUrl  = "http://musicbrainz.org/ws/2/";

        requestUrl += "method=album.getInfo&";
        requestUrl += "album=" + urlEncode(album) + "&";
        requestUrl += "artist=" + urlEncode(artist) + "&";
        requestUrl += "api_key=" + "26980b71e5c813da2c7e0156afdddd4f" + "&";
        requestUrl += "format=json";

        return requestUrl;
    }

    /** Url Encode method **/
    static public String urlEncode(String str){
        String encStr = null;
        try {
            encStr = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encStr;
    }

    /** custom error response listener **/
    private Response.ErrorListener createMyReqErrorListener(final String requestUrl, final AlbumObject albumObject) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
                Log.v("TAG", "error.getMessage()" + error.getMessage());
                if(requestUrl.equals("")){Log.v("TAG","requestUrl is empty: "+requestUrl);}   // <-- this is the case
                if(requestUrl == null){Log.v("TAG","requestUrl is null: "+requestUrl);}
                Log.v("TAG", "albumObject.albumTitle  : " + albumObject.albumTitle);
                Log.v("TAG", "albumObject.albumArtist : " + albumObject.albumArtist);
                Log.v("TAG", "getUrl(String album)    : " + getUrl(albumObject.albumTitle, albumObject.albumArtist));
            }
        };
    }

    /** custom success response listener**/
    private Response.Listener<LastFmAlbumInfoPojo> createMyReqSuccessListener(final String jsonUrl, final AlbumObject albumObject) {
        return new Response.Listener<LastFmAlbumInfoPojo>() {
            @Override

            /** Json request onResponse method **/
            public void onResponse(LastFmAlbumInfoPojo response) {

                if (null == response.album){ //null response means {"error":6,"message":"Album not found","links":[]} e.g. http://ws.audioscrobbler.com/2.0/?method=album.getInfo&album=Trilogy+Disk+2&artist=Chick+Corea+Trio&api_key=26980b71e5c813da2c7e0156afdddd4f&format=json

                    //use another api

                } else {

                    Log.v("TAG","json url is "+jsonUrl);
                    Log.v("TAG", "response.album.name T%$T%$ "+response.album.name);

                    Log.v("TAG", "LastFm got a json response object");
                    //Log.v("TAG", "response.album.images.get(2).text is "+response.album.images.get(2).text);
                    Log.v("TAG", "jsonUrl is : "+jsonUrl);
                    Log.v("TAG","album is : "+albumObject.albumArtist);
                    //Every other time I run the app, all the response objects are empty. Literally only EVERY OTHER TIME.

                    //String imageUrl="";
                    String imageUrl = response.album.images.get(4).text;


                    if(imageUrl == null || imageUrl.equals("")){

                        useApi(); //use another api

                    } else {

                        Log.v("TAG", "LastFm is making an image req");

                        //Log.v("TAG","response.results.albumMatches.albums.get(0).images.get(3).imageUrl: "+response.results.albumMatches.albums.get(0).images.get(3).imageUrl);

                        /** Make an image request **/
                        ImageRequest myImageReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                            @Override

                            /** Image request onResponse method **/
                            public void onResponse(final Bitmap response) {

                                Log.v("TAG", "LastFm got an image response");

                                if(response.getHeight() ==1 && response.getWidth() == 1){ //If there is no image, Volley return a 1x1 px black bitmap as default

                                    useApi(); //use another api

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

                                            // Update all adapters
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.v("TAG","updating adapters");
                                                    UpdateAdapters.getInstance().update();
                                                }
                                            });
                                        }
                                    }.start();
                                }
                            }
                        }, 0, 0, null, createMyReqErrorListener(imageUrl, albumObject));

                        ItunesSolo.mRequestQueue.add(myImageReq);
                    }
                }
            }
        };
    }
}
