package com.example.michael.musicplayer5;

import android.util.Log;

public class UpdateAdapters {

    private static UpdateAdapters instance = null;

    private MyListAdapterArtists myListAdapterArtists;
    private MyListAdapterTracks myListAdapterTracks;
    private MyGridViewAdapter myGridViewAdapter;

    //Constructor
    protected UpdateAdapters() { }

    //Get the current instance
    public static UpdateAdapters getInstance() {
        if (instance == null) {
            // create a new one if it doesn't exist
            instance = new UpdateAdapters();
        }
        return instance;
    }

    //Setters
    public void setAdapterOne(MyListAdapterTracks myListAdapterTracks){
        this.myListAdapterTracks = myListAdapterTracks;
    }

    public void setAdapterTwo(MyListAdapterArtists myListAdapterArtists){
        this.myListAdapterArtists = myListAdapterArtists;
    }

    public void setAdapterThree(MyGridViewAdapter myGridViewAdapter){
        this.myGridViewAdapter = myGridViewAdapter;
    }

    //Public methods
    public void update(){
        if(null != myListAdapterArtists){myListAdapterArtists.notifyDataSetChanged();Log.v("TAG", "myListAdapterArtists successfully updated ");}
        else{Log.v("TAG","myListAdapterArtists is a null reference " + myListAdapterArtists);}
        if(null != myListAdapterTracks){myListAdapterTracks.notifyDataSetChanged();Log.v("TAG", "myListAdapterTracks successfully updated ");}
        else{Log.v("TAG","myListAdapterTracks is a null reference " + myListAdapterTracks);}
        if(null != myGridViewAdapter){myGridViewAdapter.notifyDataSetChanged();Log.v("TAG", "myGridViewAdapter successfully updated ");}
        else{Log.v("TAG","myGridViewAdapter is a null reference " + myGridViewAdapter);}
    }
}
