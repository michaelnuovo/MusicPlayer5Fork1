package com.example.michael.musicplayer5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class FragmentTracks extends Fragment implements MyInterface {

    /** Fragment Variables **/
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static ListView listView;
    //public static ProperListView listView;
    static ArrayList<SongObject> songObjectList;

    static TextView currentTitleView;
    static TextView currentArtistView;

    LinearLayout TitlePanel;

    static AdapterTracks adapter;

    @Override
    public void myTask() {

        adapter.notifyDataSetChanged();

    }

    public static void task(){

        adapter.notifyDataSetChanged();

    }

    //Application app;



    public void run() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }

        });
    }

    /** Static Factory Method for Fragment Instantiation **/
    public static final FragmentTracks newInstance(ArrayList<SongObject> arrayList)
    {

        FragmentTracks f = new FragmentTracks();
        Bundle bdl = new Bundle(1);
        bdl.putParcelableArrayList(EXTRA_MESSAGE, arrayList);
        f.setArguments(bdl);
        return f;
    }

    /** On Create Method**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        songObjectList = getArguments().getParcelableArrayList(EXTRA_MESSAGE);

        View rootView = inflater.inflate(R.layout.fragment_layout_tracks, container, false);

        listView = (ListView) rootView.findViewById(R.id.fragmentListView);

        //listView.setFastScrollAlwaysVisible(true);
        //listView.setScrollBarStyle(R.drawable.scroll_style_custom);

        //View header = inflater.inflate(R.layout.list_header, container, false);
        //listView.addHeaderView(header);

        adapter = new AdapterTracks(getActivity(), R.layout.item_list_view2, songObjectList);
        listView.setAdapter(adapter);

        UpdateAdapters.getInstance().setAdapterOne(adapter,listView);



        /** Change Y direction of Drop Shadow On Play Controls Strip
        float elevation = 200;
        float density = 0.1f;
        LinearLayout shadowEdge = (LinearLayout) rootView.findViewById(R.id.playControls);
        shadowEdge.setBackgroundDrawable(new RoundRectDrawableWithShadow(
                getResources(), Color.BLACK, 0,
                elevation * density, ((elevation + 1) * density) + 1
        ));**/

        /** View initializations **/

        /*******

         STATIC MEDIA PLAYER CLASS LISTENERS

         1) StaticMediaPlayer.setSongCompletionListener()
         2) StaticMediaPlayer.setLoopButtonListener();
         3) StaticMediaPlayer.setShuffleButtonListener();
         4) StaticMediaPlayer.setPlayButtonListener();
         5) StaticMediaPlayer.setSkipForwardsListener();
         6) StaticMediaPlayer.setSkipBackwardsListener();

         ********/

        //Pass play button to static media player
        StaticMediaPlayer_OLD.SetButtonsMainActivity(
                (ToggleButton) rootView.findViewById(R.id.playButton),
                songObjectList
        );

        //Set play button listener
        StaticMediaPlayer_OLD.setPlayButtonListener();

        //Set play panel title and artist
        currentTitleView = (TextView) rootView.findViewById(R.id.currentTitle);
        currentArtistView = (TextView) rootView.findViewById(R.id.currentArtist);

        //currentTitleView.setText(songObject.title);
        //currentArtistView.setText(songObject.artist);

        TitlePanel = (LinearLayout) rootView.findViewById(R.id.activity_main_track_info);
        TitlePanelClickListener();

        Listeners();

        return rootView;
    }

    public void TitlePanelClickListener(){

        /** Title Listener **/
        TitlePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open the play panel activity from this fragment

                Intent intent = new Intent(getActivity(), ActivityPlayPanel.class);
                startActivity(intent);

                /** original code from main activity

                // Open the play panel

                Intent intent = new Intent(MainActivity.this, PlayPanelActivity.class);
                //intent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(intent);
                //overridePendingTransition(R.anim.slide_up, R.anim.dont_move);

                 **/

            }
        });
    }

    private void Listeners(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // Open the play panel activity from this fragment



                StaticMusicPlayer.tryToPlaySong(songObjectList.get(arg2)); // arg2 is a sonObject


                Intent intent = new Intent(getActivity(), ActivityPlayPanel.class);
                startActivity(intent);


                /*
                // arg2 is the position of the view which corresponds to a list array index
                // .get() method on this index will return a song object

                if (StaticMediaPlayer_OLD.playButton.isChecked() == false) { // If set to play

                    StaticMediaPlayer_OLD.playButton.setChecked(true); // set to pause
                }

                SongObject songObject = songList.get(arg2);
                StaticMediaPlayer_OLD.TryToPlaySong(songObject);
                if (StaticMediaPlayer_OLD.noSongHasBeenPlayedYet == true) {
                    StaticMediaPlayer_OLD.noSongHasBeenPlayedYet = false;
                }

                // If list view items is clicked after user has clicked back tracked a number of times
                // than that many items should be deleted from the play history list
                // since a new play list will be branched from that point

                if (StaticMediaPlayer_OLD.clickedSkipBackWardsButtonHowManyTimes > 0) {

                    int i;
                    int j = 0;

                    for (i = StaticMediaPlayer_OLD.clickedSkipBackWardsButtonHowManyTimes; i > 0; i--) {

                        StaticMediaPlayer_OLD.playHistory.remove(StaticMediaPlayer_OLD.playHistory.size() - 1); // Remove last element in the play history
                        j += 1;
                    }

                    StaticMediaPlayer_OLD.clickedSkipBackWardsButtonHowManyTimes = StaticMediaPlayer_OLD.clickedSkipBackWardsButtonHowManyTimes - j;
                }

                if (StaticMediaPlayer_OLD.playHistory.size() >= 1) {
                    // Add the clicked song to the play list
                    if (songList.get(arg2) != songList.get(StaticMediaPlayer_OLD.playHistory.get(StaticMediaPlayer_OLD.playHistory.size() - 1))) { // <---
                        StaticMediaPlayer_OLD.playHistory.add(arg2);

                    } else {
                        // do nothing
                    }
                } else {

                    StaticMediaPlayer_OLD.playHistory.add(arg2);
                }*/
            }
        });
    }
}