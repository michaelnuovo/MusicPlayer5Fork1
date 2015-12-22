package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by michael on 12/16/15.
 */
public class AmazonSolo {


    Context ctx;
    Activity activity;
    ArrayList<AlbumObject> requestList;

    public AmazonSolo(Context ctx, ArrayList<AlbumObject> requestList){
        this.ctx = ctx;
        this.activity = (Activity) ctx;
        this.requestList=requestList;
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
                Log.v("TAG", "error.getMessage() is: " + error.getMessage());
            }
        };
    }

    /** Make request **/
    public void makeRequest(){
        Log.v("TAG","Amazon is making req");
        new Thread() {
            public void run() {
                for(int i=0;i<requestList.size();i++){
                    if(requestList.get(i).albumArtURI.equals("null")){
                        fireRequest(requestList.get(i));
                        try {
                            Thread.sleep(1000);
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

                SignedRequestsHelper helper = new SignedRequestsHelper();
                Map<String, String> values = new HashMap<String, String>();
                values.put("Operation", "ItemSearch");
                values.put("AssociateTag", "mytag-20");
                values.put("SearchIndex", "Music");
                values.put("Artist", albumObject.albumArtist);
                values.put("Title", albumObject.albumTitle);
                final String url = helper.sign(values);

                Log.v("TAG","Making request "+url);
                Log.v("TAG", "album is " + albumObject.albumTitle);

                Itunes.getRequestQueue();



                StringRequest myReq = new StringRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<String>()
                        {

                            @Override
                            public void onResponse(String response) {

                                //final String value="";
                                final String asinValue = parseXml(response, "/ItemSearchResponse/Items/Item/ASIN");
                                //final String imageUrl = "";
                                final String imageUrl = "http://images.amazon.com/images/P/" + asinValue + ".01._SCLZZZZZZZ_.jpg";

                              if(asinValue == ""){

                                    Log.v("TAG","-------------------------------------");
                                    Log.v("TAG","There is no ASIN number, lets try iTunes");
                                    Log.v("TAG","parse value is: "+asinValue);
                                    Log.v("TAG","the amazon album title is: "+albumObject.albumTitle);
                                    Log.v("TAG","the xml url is: "+url);
                                    Log.v("TAG","the image url is: "+imageUrl);
                                    Log.v("TAG","-------------------------------------");

                                    //Itunes itunes = new Itunes(songObject, songObject.albumArtURI,albumId,ctx, albumTitle, artist,activity);
                                    //itunes.makeRequest();

                               }

                                new Thread() {
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                                //if(!asinValue.equals("")){ //If there is an ASIN number do this, else don't do it
                                    ImageRequest myImageReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(final Bitmap response) {
                                            new Thread() {
                                                public void run() {

                                                    //If there is no image, Volley return a 1x1 px black bitmap as default
                                                    if(response.getHeight() ==1 && response.getWidth() == 1){
                                                        Log.v("TAG","bit map is a deafault response");
                                                        //use anotehr thigny
                                                    } else {

                                                        Bitmap emptyBitmap = Bitmap.createBitmap(response.getWidth(), response.getHeight(), response.getConfig());
                                                        if (response.sameAs(emptyBitmap)) {
                                                            Log.v("TAG","bit map is blank");
                                                        }

                                                        // Save the bitmap to disk, return an image path
                                                        SaveBitMapToDisk saveImage = new SaveBitMapToDisk();
                                                        saveImage.SaveImage(response, "myalbumart");
                                                        String imagePathData = saveImage.getImagePath();

                                                        // Update the image path to Android meta data
                                                        MediaStoreInterface mediaStore = new MediaStoreInterface(ctx);
                                                        mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(Long.valueOf(albumObject.albumId), imagePathData);

                                                        Log.v("TAG", "Writing album : " + albumObject.albumTitle);
                                                        Log.v("TAG","Writing path : "+imagePathData);

                                                        //update uri path
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

                                                                Log.v("TAG","updating adapters ");
                                                                UpdateAdapters.getInstance().update();

                                                            }
                                                        });
                                                    }


                                                }
                                            }.start();
                                        }
                                    }, 0, 0, null, createMyReqErrorListener(imageUrl));

                                    Itunes.mRequestQueue.add(myImageReq);
                                //}
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
                 100000,
                 DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                 DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                 **/

                //myReq.setRetryPolicy(new DefaultRetryPolicy(5000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



                Itunes.mRequestQueue.add(myReq);
                //mRequestQueue.add(myReq);




    }

    public String processData(String response){

        return "test run id";
    }

    public String parseXml(String response, String path){

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document dDoc = builder.parse(new InputSource(new StringReader(response)));

            XPath xPath = XPathFactory.newInstance().newXPath();
            //Node node = (Node) xPath.evaluate("/Items/Item/ASIN", dDoc, XPathConstants.NODE);
            //System.out.println(node.getNodeValue());
            String value = xPath.evaluate(path, dDoc);
            return value;
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
            return null;
        }
    }

    public String getFromKeyWordSearch(String albumTitle){

        SignedRequestsHelper helper = new SignedRequestsHelper();
        Map<String, String> values = new HashMap<String, String>();
        values.put("Operation", "ItemSearch");
        values.put("AssociateTag", "mytag-20");
        values.put("SearchIndex", "Music");
        values.put("Keywords",albumTitle);
        //values.put("Artist", artist);
        //values.put("Title", albumTitle);
        String xmlUrl = helper.sign(values);

        Log.v("TAG","xml url using key words is "+xmlUrl);

        String value = parseXml(xmlUrl, "ItemSearchResponse/Items/Item/ASIN");

        String imageUrl = "http://images.amazon.com/images/P/" + value + ".01._SCLZZZZZZZ_.jpg";

        Log.v("TAG","The image url using keywords is: "+imageUrl);

        return imageUrl;
    }



}
