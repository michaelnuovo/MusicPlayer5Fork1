package com.example.michael.musicplayer5;

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
public class AlbumArtLogic {

    Long albumId;
    private static RequestQueue mRequestQueue;
    Context ctx;
    String albumTitle;

    /** Constructor **/
    public AlbumArtLogic(Long albumId, Context ctx, String albumTitle)
    {
        this.albumId = albumId;
        this.ctx = ctx;
        this.albumTitle = albumTitle;
    }

    /** Make a Json request **/
    public void makeRequest(String jsonObjectArrayUrl){
        //String JsonUrl = "https://itunes.apple.com/search?term=michael+jackson&entity=album";
        getRequestQueue();
        GsonRequest<SongInfo> myReq = new GsonRequest<>(
                Request.Method.GET,
                jsonObjectArrayUrl,
                SongInfo.class,
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
    private Response.Listener<SongInfo> createMyReqSuccessListener(final Long albumId, final String jsonObjectArrayUrl) {
        return new Response.Listener<SongInfo>() {
            @Override

            /** Json request onResponse method **/
            public void onResponse(SongInfo response) {

                // find in the json object array the json object whose attribute "collectionName" contains albumTitle
                Log.v("TAG","URL to parse: "+jsonObjectArrayUrl);
                String imageUrl="Cannot find the collection";
                for(int i=0;i<response.results.size();i++){
                    if(response.results.get(i).collectionName.contains(albumTitle)){
                        imageUrl = response.results.get(i).artworkUrl30;
                        break;
                    }
                }

                // We need a higher resolution, so we tweak the URL so that this:
                // artworkUrl30":"http://is4.mzstatic.com/image/thumb/Music6/v4/68/b5/27/68b5273f-7044-8dbb-4ad1-82473837a136/source/30x30bb.jpg
                // becomes this:
                // http://is4.mzstatic.com/image/thumb/Music6/v4/68/b5/27/68b5273f-7044-8dbb-4ad1-82473837a136/source/1300x1300bb.jpg
                // https://itunes.apple.com/search?term=michael+jackson


                String newUrl = imageUrl.replace("30x30bb","1300x1300bb"); // we need the higher resolution version
                Log.v("TAG","url request: "+newUrl);
                Log.v("TAG","album title is: "+albumTitle);

                /** Make an image request **/
                ImageRequest myImageReq = new ImageRequest(newUrl, new Response.Listener<Bitmap>() {
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

                                // Update all adapters (not yet implemented)
                                Adapters adapter = new Adapters();
                                adapter.updateAll();


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
                }, 0, 0, null, createMyReqErrorListener(newUrl));

                mRequestQueue.add(myImageReq);
            }
        };
    }
}
