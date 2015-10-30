package com.example.michael.musicplayer5;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ToggleButton;
import java.util.ArrayList;



public class MyFragment extends Fragment {

    /** Fragment Variables **/
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    static ListView listView;
    static ArrayList<SongObject> songList = MainActivity.songList;

    /** Button References **/
    static ToggleButton playButton;
    static ToggleButton skipForwardsButton;
    static ToggleButton skipBackwardsButton;
    static ToggleButton shuffleButton;
    static ToggleButton loopButton;

    /** Media Player Engine Variables **/
    static MediaPlayer mediaPlayer = MainActivity.mediaPlayer;
    static ArrayList<Integer> playHistory =  MainActivity.playHistory;
    static Boolean shuffleOn = MainActivity.shuffleOn;
    static Boolean loopModeOn = MainActivity.loopModeOn;
    static Boolean pressedPlay = MainActivity.pressedPlay;
    static int clickedSkipBackWardsButtonHowManyTimes  = MainActivity.clickedSkipBackWardsButtonHowManyTimes;
    static Boolean noSongHasBeenPlayedYet = MainActivity.noSongHasBeenPlayedYet;

    /** Static Factory Method for Fragment Instantiation **/
    public static final MyFragment newInstance(ArrayList<SongObject> arrayList)
    {
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putParcelableArrayList(EXTRA_MESSAGE, arrayList);
        f.setArguments(bdl);
        return f;
    }

    /** On Create Method**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayList<SongObject> arrayList = getArguments().getParcelableArrayList(EXTRA_MESSAGE);

        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);

        /** Button Initializations **/
        playButton = (ToggleButton) rootView.findViewById(R.id.playButton);
        skipForwardsButton = (ToggleButton) rootView.findViewById(R.id.skipForwards);
        skipBackwardsButton = (ToggleButton) rootView.findViewById(R.id.skipBackwards);
        shuffleButton = (ToggleButton) rootView.findViewById(R.id.shuffle);
        loopButton = (ToggleButton) rootView.findViewById(R.id.loopList);

        listView = (ListView) rootView.findViewById(R.id.fragmentListView);

        ListAdapter adapter = new ListAdapter(getActivity(), R.layout.list_view_item_title, arrayList);
        listView.setAdapter(adapter);

        /** View initializations **/

        Listeners();

        return rootView;
    }


    private void Listeners() {

        // If a song finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {

                if (loopModeOn == true) {

                    mediaPlayer.start();
                } else {

                    MainActivity.PlayAndIndexASong();
                }
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("TAG loop button", "clicked");
                if (loopModeOn == false) {
                    // if media player loops mode is off --> turn on
                    //mediaPlayer.setLooping(true); // this mode doesn't work on certain versions of android http://stackoverflow.com/questions/28566268/mediaplayer-wont-loop-setlooping-doesnt-work
                    loopModeOn = true;
                    Log.v("TAG loop mode ", "on");
                } else {
                    // turn loop mode off
                    //mediaPlayer.setLooping(false);
                    loopModeOn = false;
                    Log.v("TAG loop mode ", "off");
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleOn == false) {
                    shuffleOn = true;
                } else {
                    shuffleOn = false;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // arg2 is the position of the view which corresponds to a list array index
                // .get() method on this index will return a song object

                if (playButton.isChecked() == false) { // If set to play

                    playButton.setChecked(true); // set to pause
                }

                MainActivity.TryToPlaySong(songList.get(arg2).data);
                if (noSongHasBeenPlayedYet == true) {
                    noSongHasBeenPlayedYet = false;
                }

                // If list view items is clicked after user has clicked back tracked a number of times
                // than that many items should be deleted from the play history list
                // since a new play list will be branched from that point

                if (clickedSkipBackWardsButtonHowManyTimes > 0) {

                    int i;
                    int j = 0;

                    for (i = clickedSkipBackWardsButtonHowManyTimes; i > 0; i--) {

                        playHistory.remove(playHistory.size() - 1); // Remove last element in the play history
                        j += 1;
                    }

                    clickedSkipBackWardsButtonHowManyTimes = clickedSkipBackWardsButtonHowManyTimes - j;
                }

                if (playHistory.size() >= 1) {
                    // Add the clicked song to the play list
                    if (songList.get(arg2) != songList.get(playHistory.get(playHistory.size() - 1))) { // <---
                        playHistory.add(arg2);

                    } else {
                        // do nothing
                    }
                } else {

                    playHistory.add(arg2);
                }
            }
        });

        /** Play Button Listener **/
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(noSongHasBeenPlayedYet == true){

                    MainActivity.PlayAndIndexASong();

                } else {

                    if(pressedPlay == true){
                        pressedPlay = false;
                        mediaPlayer.start();

                    }else{
                        Log.v("TAG: ","pause");
                        pressedPlay = true;
                        mediaPlayer.pause();
                    }
                }
            }
        });

        /** Skip Forwards Listener **/
        skipForwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playButton.isChecked() == false){
                    playButton.setChecked(true);
                }

                if(noSongHasBeenPlayedYet == true){
                    MainActivity.PlayAndIndexASong();
                    noSongHasBeenPlayedYet = false;
                } else {
                    MainActivity.PlayAndIndexASong();
                }
            }
        });

        /** Skip backwards Listener **/
        skipBackwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playHistory.size() == 0) { // If there are no songs in the play history

                    // Do nothing

                } else {

                    // If current position is greater 5 seconds, restart
                    if (mediaPlayer.getCurrentPosition() > 10000) {

                        if (playHistory.size() > 1) {

                            MainActivity.TryToPlaySong(songList.get(playHistory.get(playHistory.size() - 1 - clickedSkipBackWardsButtonHowManyTimes)).data); // Get the last song
                        } else {
                            Log.v("Replay", "");
                            MainActivity.TryToPlaySong(songList.get(playHistory.get(0)).data); // Get first song if there is only one song
                            Log.v("Replay", "");
                        }

                    } else {

                        // Avoid out of bounds error with play history
                        if (playHistory.size() - 1 - clickedSkipBackWardsButtonHowManyTimes > 0) {

                            Log.v("AAA ,", String.valueOf(playHistory.size() - 1));
                            Log.v("BBB ", String.valueOf(clickedSkipBackWardsButtonHowManyTimes));

                            clickedSkipBackWardsButtonHowManyTimes += 1;

                            int songIndex = playHistory.size() - 1;
                            songIndex = songIndex - clickedSkipBackWardsButtonHowManyTimes;

                            MainActivity.TryToPlaySong(songList.get(playHistory.get(songIndex)).data);
                        } else {
                            // do nothing
                            Log.v("CCC ", "");
                        }
                    }
                }
            }
        });
    }
}