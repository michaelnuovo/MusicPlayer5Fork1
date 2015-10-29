package com.example.michael.musicplayer5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;


public class MyFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    ListView lv;
    ArrayList<SongObject> arrayList;

    public static final MyFragment newInstance(ArrayList<SongObject> arrayList)
    {
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putParcelableArrayList(EXTRA_MESSAGE, arrayList);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayList<SongObject> arrayList = getArguments().getParcelableArrayList(EXTRA_MESSAGE);

        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);

        lv = (ListView) rootView.findViewById(R.id.fragmentListView);

        ListAdapter adapter = new ListAdapter(getActivity(), R.layout.list_view_item_title, arrayList);
        lv.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // arg2 is the position of the view which corresponds to a list array index
                // .get() method on this index will return a song object

                if (MainActivity.playButton.isChecked() == false) { // If set to play

                    MainActivity.playButton.setChecked(true); // set to pause
                }

                MainActivity.TryToPlaySong(arrayList.get(arg2).data);
                if (MainActivity.noSongHasBeenPlayedYet == true) {
                    MainActivity.noSongHasBeenPlayedYet = false;
                }

                // If list view items is clicked after user has clicked back tracked a number of times
                // than that many items should be deleted from the play history list
                // since a new play list will be branched from that point

                if (MainActivity.clickedSkipBackWardsButtonHowManyTimes > 0) {

                    int i;
                    int j=0;

                    for (i = MainActivity.clickedSkipBackWardsButtonHowManyTimes; i > 0; i--) {

                        MainActivity.playHistory.remove(MainActivity.playHistory.size() - 1); // Remove last element in the play history
                        j+=1;
                    }

                    MainActivity.clickedSkipBackWardsButtonHowManyTimes = MainActivity.clickedSkipBackWardsButtonHowManyTimes - j;
                }

                if(MainActivity.playHistory.size()>=1){
                    // Add the clicked song to the play list
                    if(arrayList.get(arg2) != arrayList.get(MainActivity.playHistory.get(MainActivity.playHistory.size()-1))){ // <---
                        MainActivity.playHistory.add(arg2);

                    }else{
                        // do nothing
                    }
                }else{

                    MainActivity.playHistory.add(arg2);
                }
            }
        });
    }



}