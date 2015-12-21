package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;


/**
 * Created by michael on 12/16/15.
 */
public class Amazon {

    Long albumId;
    //private static RequestQueue mRequestQueue;
    Context ctx;
    String albumTitle;
    String artist;
    SongObject songObject;
    //public static RequestQueue mRequestQueue;
    Activity activity;

    /** Constructor **/
    public Amazon(Long albumId, Context ctx, String albumTitle, String artist, SongObject songObject, Activity activity)
    {
        this.albumId = albumId;
        this.ctx = ctx;
        this.albumTitle = albumTitle;
        this.artist = artist;
        this.songObject = songObject;
        this.activity = activity;
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

    /** Make a Json request **/

    public void makeRequest(final String url){

        Itunes.getRequestQueue();
        //getRequestQueue();

        //Log.v("TAG","Submitting request to Amazon at: "+url);
        //Log.v("TAG","Album title is: "+albumTitle);

        //url = "http://webservices.amazon.com/onca/xml?AWSAccessKeyId=AKIAJ6L6R4KOPIYIUXUA&Artist=Bill%20Evans%20Trio&AssociateTag=mytag-20&Operation=ItemSearch&SearchIndex=Music&Timestamp=2015-12-18T07%3A03%3A45Z&Title=Sunday%20At%20the%20Village%20Vanguard&Signature=uB0u82l7gdu6n3ICnqfepYC5tu6hj7YHdyN%2FbKGHUFk%3D";

        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {

                        //Log.v("TAG","Response is : "+response);
                        //Log.v("TAG","Album title from response is: "+albumTitle);

                        //so we need to parse the response

                        final String value = parseXml(response);
                        Log.v("TAG","parsed value is: "+value);
                        Log.v("TAG","Album is: "+albumTitle);
                        Log.v("TAG","Artist is: "+artist);
                        Log.v("TAG","Xm url is: "+url);

                        final String imageUrl = "http://images.amazon.com/images/P/" + value + ".01._SCLZZZZZZZ_.jpg";



                        ImageRequest myImageReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                            @Override

                            /** Image request onResponse method **/
                            public void onResponse(final Bitmap response) {

                                new Thread() {
                                    public void run() {


                                        Log.v("TAG","the amazon album title is: "+albumTitle);
                                        Log.v("TAG","the xml url is: "+url);
                                        Log.v("TAG","the image url is: "+imageUrl);

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
                                    }
                                }.start();
                            }
                        }, 0, 0, null, createMyReqErrorListener(imageUrl));

                        Itunes.mRequestQueue.add(myImageReq);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        /**
        myReq.setRetryPolicy(new DefaultRetryPolicy(
         15000, // MY_SOCKET_TIMEOUT_MS
         DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
         DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
         **/

        Itunes.mRequestQueue.add(myReq);
        //mRequestQueue.add(myReq);
    }

    public String processData(String response){

        return "test run id";
    }

    public String parseXml(String response){

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document dDoc = builder.parse(new InputSource(new StringReader(response)));

            XPath xPath = XPathFactory.newInstance().newXPath();
            //Node node = (Node) xPath.evaluate("/Items/Item/ASIN", dDoc, XPathConstants.NODE);
            //System.out.println(node.getNodeValue());
            String value = xPath.evaluate("/ItemSearchResponse/Items/Item/ASIN", dDoc);
            return value;
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
            return null;
        }
    }


}
