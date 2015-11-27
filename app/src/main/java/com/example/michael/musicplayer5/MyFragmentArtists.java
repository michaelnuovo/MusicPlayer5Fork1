package com.example.michael.musicplayer5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class MyFragmentArtists extends Fragment {

    /** Fragment Variables **/
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    static ListView listView;
    static ArrayList<ArtistObject> artistObjectList;

    /** Static Factory Method for Fragment Instantiation **/
    public static final MyFragmentArtists newInstance(ArrayList<ArtistObject> arrayList)
    {
        MyFragmentArtists f = new MyFragmentArtists();
        Bundle bdl = new Bundle(1);
        bdl.putParcelableArrayList(EXTRA_MESSAGE, arrayList);
        f.setArguments(bdl);
        return f;
    }

    /** On Create Method**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        artistObjectList = getArguments().getParcelableArrayList(EXTRA_MESSAGE);

        View rootView = inflater.inflate(R.layout.fragment_layout_artists, container, false);

        listView = (ListView) rootView.findViewById(R.id.fragmentListViewArtists);

        MyListAdapterArtists adapter = new MyListAdapterArtists(getActivity(), R.layout.item_list_view_artists, artistObjectList);
        listView.setAdapter(adapter);


        return rootView;
    }
}
