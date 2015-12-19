package com.example.michael.musicplayer5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class MyFragmentAlbums extends Fragment {

    /** Fragment Variables **/
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    static GridView gridView;
    static ArrayList<AlbumObject> albumObjectList;

    /** Static Factory Method for Fragment Instantiation **/
    public static final MyFragmentAlbums newInstance(ArrayList<AlbumObject> arrayList)
    {
        MyFragmentAlbums f = new MyFragmentAlbums();
        Bundle bdl = new Bundle(1);
        bdl.putParcelableArrayList(EXTRA_MESSAGE, arrayList);
        f.setArguments(bdl);
        return f;
    }

    /** On Create Method**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        albumObjectList = getArguments().getParcelableArrayList(EXTRA_MESSAGE);

        View rootView = inflater.inflate(R.layout.fragment_layout_grid, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView);

        MyGridViewAdapter gridAdapter = new MyGridViewAdapter(getActivity(), R.layout.item_grid_view, albumObjectList);
        gridView.setAdapter(gridAdapter);

        UpdateAdapters.getInstance().setAdapterThree(gridAdapter);

        return rootView;
    }
}
