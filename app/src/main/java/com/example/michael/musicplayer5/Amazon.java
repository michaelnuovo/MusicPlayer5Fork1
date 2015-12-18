package com.example.michael.musicplayer5;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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

    /** Constructor **/
    public Amazon(Long albumId, Context ctx, String albumTitle, String artist)
    {
        this.albumId = albumId;
        this.ctx = ctx;
        this.albumTitle = albumTitle;
        this.artist = artist;
    }

    /** Make a Json request **/
    public void makeRequest(String url){
        //String JsonUrl = "https://itunes.apple.com/search?term=michael+jackson&entity=album";
        Itunes.getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    /** On response **/
                    @Override
                    public void onResponse(String response) {
                        String id = processData(response);
                        Log.v("TAG","The Amazon ID is: "+id);

                        //Parse response for a single node value
                        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                        try {
                            DocumentBuilder builder = domFactory.newDocumentBuilder();
                            //Document dDoc = builder.parse("E:/test.xml");
                            Document dDoc = builder.parse(new InputSource(new StringReader(response)));
                            XPath xPath = XPathFactory.newInstance().newXPath();
                            Node node = (Node) xPath.evaluate("ItemSearchResponse/Items/Item/ASIN", dDoc, XPathConstants.NODE);
                            Log.v("TAG","node value is: "+node.getNodeValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // handle error response
                    }
                });

        Itunes.mRequestQueue.add(myReq);
    }

    public String processData(String response){

        return "test run id";
    }
}
