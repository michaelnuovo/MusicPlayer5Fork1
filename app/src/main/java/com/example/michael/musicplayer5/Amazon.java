package com.example.michael.musicplayer5;

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
    //public static RequestQueue mRequestQueue;

    /** Constructor **/
    public Amazon(Long albumId, Context ctx, String albumTitle, String artist)
    {
        this.albumId = albumId;
        this.ctx = ctx;
        this.albumTitle = albumTitle;
        this.artist = artist;
    }

    /** Static request queue (we don't want multiple request queue objects)
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.getAppContext());
        }
        return mRequestQueue;
    }**/

    /** Make a Json request **/
    public void makeRequest(String url){

        Itunes.getRequestQueue();
        //getRequestQueue();

        Log.v("TAG","Submitting request to Amazon at: "+url);
        Log.v("TAG","Album title is: "+albumTitle);

        //url = "http://webservices.amazon.com/onca/xml?AWSAccessKeyId=AKIAJ6L6R4KOPIYIUXUA&Artist=Bill%20Evans%20Trio&AssociateTag=mytag-20&Operation=ItemSearch&SearchIndex=Music&Timestamp=2015-12-18T07%3A03%3A45Z&Title=Sunday%20At%20the%20Village%20Vanguard&Signature=uB0u82l7gdu6n3ICnqfepYC5tu6hj7YHdyN%2FbKGHUFk%3D";

        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {

                        Log.v("TAG","Response is : "+response);
                        Log.v("TAG","Album title from response is: "+albumTitle);

                        //so we need to parse the response

                        String asid = getAsid(response);

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

    public String getAsid(String response){

        //Parse response for a single node value
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            //Document dDoc = builder.parse("E:/test.xml");
            Document dDoc = builder.parse(new InputSource(new StringReader(response)));
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.evaluate("ItemSearchResponse/Items/Item/ASIN", dDoc, XPathConstants.NODE);
            Log.v("TAG","node value is: "+node.getNodeValue());
            return node.getNodeValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
