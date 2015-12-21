package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;

/**
 * Created by michael on 12/20/15.
 */
public class SpotifySolo {

    Context ctx;
    Activity activity;
    ArrayList<SongObject> requestList;

    public SpotifySolo(Context ctx, ArrayList<SongObject> requestList){
        this.ctx=ctx;
        this.requestList=requestList;
        this.activity = (Activity) ctx;
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

    /** Fire a Json request **/
    public void fireRequest(SongObject songObject){
        //String JsonUrl = "https://itunes.apple.com/search?term=michael+jackson&entity=album";
        String jsonObjectArrayUrl = spotifyUrl(songObject.artist,songObject.albumTitle);
        Itunes.getRequestQueue();
        GsonRequest<SpotifyAlbumInfo> myReq = new GsonRequest<>(
                Request.Method.GET,
                jsonObjectArrayUrl,
                SpotifyAlbumInfo.class,
                null,
                createMyReqSuccessListener(Long.parseLong(songObject.albumID), jsonObjectArrayUrl, songObject),
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
    private Response.Listener<SpotifyAlbumInfo> createMyReqSuccessListener(final Long albumId, final String jsonUrl, final SongObject songObject) {
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
                    Log.v("TAG","The album is "+songObject.albumTitle);
                    Log.v("TAG","Trying Amazon...");
                } else {
                    imageUrl = response.albums.items.get(0).images.get(0).url; // get the first album url
                    Log.v("TAG","Yes, it does exist on Spotify.");
                    Log.v("TAG","The album is "+songObject.albumTitle);
                    Log.v("TAG","The image url is "+imageUrl);
                }

                /** Try to use native image embedded in the music file **/
                if(null == imageUrl){

                    //BitMap bm = getAlbumArtFromMusicFile();
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



    static public String spotifyUrl(String artist, String album){

        //Log.v("TAG","artist is "+artist);
        //Log.v("TAG","album is "+album);

        String splitNameArtist = artist.replace(" ", "%20");
        String splitNameAlbum = album.replace(" ", "%20");

        //Log.v("TAG","splitNameArtist is "+splitNameArtist);
        //Log.v("TAG","splitNameAlbum is "+splitNameAlbum);

        String jsonUrl = "https://api.spotify.com/v1/search?q=album:arrival%20artist:abba&type=album" + "&limit=50"; // limit is 50 per page
        jsonUrl = jsonUrl.replace("abba",splitNameArtist);
        jsonUrl = jsonUrl.replace("arrival",splitNameAlbum);

        //Log.v("TAG", "#@R#" + jsonUrl);

        return jsonUrl;
    }
}