package com.example.michael.musicplayer5;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * Created by michael on 11/28/15.
 */
public class VolleyGson {

    static RequestQueue queue = Volley.newRequestQueue(MainActivity.getAppContext());

    /*
    private void startRequest() {
        GsonRequest<MyClass> jsObjRequest = new GsonRequest<MyClass>(
                Request.Method.GET,
                getString(R.string.rest_url),
                "bleh", //MyRestResponse.class,
                null,
                createMyReqSuccessListener(),
                createMyReqErrorListener());
        queue.add(jsObjRequest);
    }

    GsonRequest<MyClass> myReq = new GsonRequest<MyClass>(
            Request.Method.GET,
            "http://JSONURL/",
            TagList.class,
            createMyReqSuccessListener(),
            createMyReqErrorListener());

    public void addReq(){queue.add(myReq);}

*/

    private Response.Listener<MyClass> createMyReqSuccessListener() {
        return new Response.Listener<MyClass>() {
            @Override
            public void onResponse(MyClass response) {
                // Do whatever you want to do with response;
                // Like response.tags.getListing_count(); etc. etc.
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
            }
        };
    }
}
