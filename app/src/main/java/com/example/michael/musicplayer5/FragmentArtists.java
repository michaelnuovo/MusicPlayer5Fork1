package com.example.michael.musicplayer5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentArtists extends Fragment {

    /** Fragment Variables **/
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    static ListView listView;
    static ArrayList<ArtistObject> artistObjectList;

    /** Static Factory Method for Fragment Instantiation **/
    public static final FragmentArtists newInstance(ArrayList<ArtistObject> arrayList)
    {
        FragmentArtists f = new FragmentArtists();
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

        AdapterArtists adapter = new AdapterArtists(getActivity(), R.layout.item_list_view_artists, artistObjectList);
        listView.setAdapter(adapter);

        Log.v("TAG", "artist adapter value is " + String.valueOf(adapter));

        UpdateAdapters.getInstance().setAdapterTwo(adapter);

        Listeners();

        return rootView;
    }

    private void Listeners(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                Intent intent = new Intent(getActivity(), ActivityPlayPanel.class);
                startActivity(intent);
            }
        });
    }
}
