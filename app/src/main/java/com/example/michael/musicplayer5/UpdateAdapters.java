package com.example.michael.musicplayer5;

import android.util.Log;
import android.widget.ListView;

public class UpdateAdapters {

    private static UpdateAdapters instance = null;

    private AdapterArtists adapterArtists;
    private AdapterTracks adapterTracks;
    private AdapterGridView adapterGridView;

    private ListView MyListViewTracks;

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
    public void setAdapterOne(AdapterTracks adapterTracks, ListView MyListViewTracks){
        this.adapterTracks = adapterTracks;
        this.MyListViewTracks=MyListViewTracks;
    }

    public void setAdapterTwo(AdapterArtists adapterArtists){
        this.adapterArtists = adapterArtists;
    }

    public void setAdapterThree(AdapterGridView adapterGridView){
        this.adapterGridView = adapterGridView;
    }

    //Public methods
    public void update(){

        if(null != adapterArtists) {
            adapterArtists.notifyDataSetChanged();
            Log.v("TAG", "myListAdapterArtists successfully updated ");
        } else {
            Log.v("TAG","myListAdapterArtists is a null reference ");}
                                                          //http://stackoverflow.com/questions/32079931/difference-between-listview-invalidate-vs-invalidateviews
        if(null != adapterTracks) {                 //http://stackoverflow.com/questions/10676720/is-there-any-difference-between-listview-invalidateviews-and-adapter-notify
                  //refreshes the views in the listview
            adapterTracks.notifyDataSetChanged();   //Notifying the dataset changed will cause the listview to invoke your adapters methods again to adjust scrollbars, regenerate item views, etc...
            //MyListViewTracks.invalidate();
            Log.v("TAG", "myListAdapterTracks successfully updated ");
        } else {
            Log.v("TAG","myListAdapterTracks is a null reference ");}

        if(null != adapterGridView) {
            adapterGridView.notifyDataSetChanged();
            Log.v("TAG", "myGridViewAdapter successfully updated ");
        } else {
            Log.v("TAG","myGridViewAdapter is a null reference ");
        }
    }
}
