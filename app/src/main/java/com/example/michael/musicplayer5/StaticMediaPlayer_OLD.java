package com.example.michael.musicplayer5;


import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public final class StaticMediaPlayer_OLD {

    /** Internal Variables **/
    static int max;
    static int min;
    static int clickedSkipBackWardsButtonHowManyTimes = 0;
    static Boolean noSongHasBeenPlayedYet = true;
    static Boolean shuffleOn = false;
    static Boolean loopModeOn = false;
    static Boolean pressedPlay = false;
    static android.media.MediaPlayer mediaPlayer = new android.media.MediaPlayer();
    static ArrayList<Integer> playHistory = new ArrayList<>();
    static Random randomUtil = new Random();
    static String currentTitle;
    static String currentArtist;
    static SongObject currentSongObect;

    /** External Variables **/
    static ToggleButton playButton;
    static Button skipForwardsButton;
    static Button skipBackwardsButton;
    static ToggleButton shuffleButton;
    static ToggleButton loopButton;

    static ArrayList<com.example.michael.musicplayer5.SongObject> songObjectList = ActivityMain.mainList;

    private StaticMediaPlayer_OLD() {

        // A private constructor that does nothing

    }

    public static void SetButtonsMainActivity(ToggleButton mplayButton,
                                              ArrayList<com.example.michael.musicplayer5.SongObject> msongObjectList){
        playButton = mplayButton;
        songObjectList = msongObjectList;
    }

    public static void SetButtonsPlayPanel(ToggleButton mplayButton,
                                           Button mskipForwardsButton,
                                           Button mskipBackwardsButton
                                           //ToggleButton mshuffleButton,
                                           //ToggleButton mloopButton,

                                           //TextView mcurrentTitleView,
                                           //TextView mcurrentArtistView,
                                           ){

        playButton = mplayButton;
        skipForwardsButton = mskipForwardsButton;
        skipBackwardsButton = mskipBackwardsButton;
        //shuffleButton = mshuffleButton;
        //loopButton = mloopButton;

    }

    /*
    public static void SetButtonsMainActivity(
    ToggleButton mplayButton,
                                              ToggleButton mskipForwardsButton,
                                              ToggleButton mskipBackwardsButton,
                                              ToggleButton mshuffleButton,
                                              ToggleButton mloopButton,

                                              TextView mcurrentTitleView,
                                              TextView mcurrentArtistView,

                                              ArrayList<com.example.michael.musicplayer5.SongObject> msongObjectList
                                              ){
        playButton = mplayButton;
        skipForwardsButton = mskipForwardsButton;
        skipBackwardsButton = mskipBackwardsButton;
        shuffleButton = mshuffleButton;
        loopButton = mloopButton;

        currentTitleView = mcurrentTitleView;
        currentArtistView = mcurrentArtistView;

        songObjectList = msongObjectList;
    }*/



    /*******

     STATIC MEDIA PLAYER CLASS LISTENERS

     1) StaticMediaPlayer.setSongCompletionListener()
     2) StaticMediaPlayer.setLoopButtonListener();
     3) StaticMediaPlayer.setShuffleButtonListener();
     4) StaticMediaPlayer.setPlayButtonListener();
     5) StaticMediaPlayer.setSkipForwardsListener();
     6) StaticMediaPlayer.setSkipBackwardsListener();

     ********/

    public static void setSongCompletionListener(){

        // If a song finishes playing
        mediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {

            public void onCompletion(android.media.MediaPlayer mp) {

                if (loopModeOn == true) {

                    mediaPlayer.start();
                } else {

                    PlayAndIndexASong();
                }
            }
        });

    }

    public static void setLoopButtonListener(){

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
    }

    public static void setShuffleButtonListener(){

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
    }

    public static void setPlayButtonListener(){

        /** Play Button Listener **/
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ","play button clicked");

                if(noSongHasBeenPlayedYet == true) {

                    PlayAndIndexASong();

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
    }

    public static void setSkipForwardsListener() {

        /** Skip Forwards Listener **/
        skipForwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ","forwards button clicked");

                if(playButton.isChecked() == false){
                    playButton.setChecked(true);
                }

                if(noSongHasBeenPlayedYet == true){
                    PlayAndIndexASong();
                    noSongHasBeenPlayedYet = false;
                } else {
                    PlayAndIndexASong();
                }
            }
        });
    }

    public static void setSkipBackwardsListener() {

        /** Skip backwards Listener **/
        skipBackwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ","back button clicked");

                if (playHistory.size() == 0) { // If there are no songs in the play history

                    // Do nothing

                } else {

                    // If current position is greater 5 seconds, restart
                    if (mediaPlayer.getCurrentPosition() > 10000) {

                        if (playHistory.size() > 1) {

                            SongObject songObject = songObjectList.get(playHistory.get(playHistory.size() - 1 - clickedSkipBackWardsButtonHowManyTimes));
                            TryToPlaySong(songObject); // Get the last song
                        } else {
                            Log.v("Replay", "");
                            SongObject songObject = songObjectList.get(playHistory.get(0));
                            TryToPlaySong(songObject); // Get first song if there is only one song
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

                            SongObject songObject = songObjectList.get(playHistory.get(songIndex));
                            TryToPlaySong(songObject);
                        } else {
                            // do nothing
                            Log.v("CCC ", "");
                        }
                    }
                }
            }
        });
    }

    public static void PlayAndIndexASong() {

        if(clickedSkipBackWardsButtonHowManyTimes > 0){

            Log.v("Play history size: ",String.valueOf(playHistory.size()));
            Log.v("Clicked back button: ",String.valueOf(clickedSkipBackWardsButtonHowManyTimes));
            Log.v("Difference: ", String.valueOf(playHistory.size() - clickedSkipBackWardsButtonHowManyTimes));

            int songIndex = playHistory.size() - 1 - clickedSkipBackWardsButtonHowManyTimes + 1;
            SongObject songObject = songObjectList.get(playHistory.get(songIndex));
            TryToPlaySong(songObject);
            clickedSkipBackWardsButtonHowManyTimes -=1;

        }else{

            if (shuffleOn == true) {

                GetRandomSongIndex();

            } else {

                GetNextSongIndex();
            }
        }
    }

    public static void GetNextSongIndex(){

        if (noSongHasBeenPlayedYet == true) {

            SongObject songObject = songObjectList.get(0);
            TryToPlaySong(songObject);
            noSongHasBeenPlayedYet = false;

            playHistory.add(0);


        } else {
            Log.v("HELLO WORLD", "HELLO WORLD");
            // To avoid out of bounds error
            if(playHistory.get(playHistory.size()-1) // returns an index
                    == songObjectList.size()-1) // the last index
            { // <--

                Log.v("TAG", "HERE");

                SongObject songObject = songObjectList.get(0);
                TryToPlaySong(songObject);


                playHistory.add(0);

            } else {

                Log.v("TAG","HERE AGAIN");

                int songIndex = playHistory.get(playHistory.size() - 1); // get the last song index in the play history
                songIndex = songIndex + 1; // add one to it

                SongObject songObject = songObjectList.get(songIndex);
                TryToPlaySong(songObject); // play it

                playHistory.add(songIndex); // add it to the play history

            }
        }
    }

    public static void GetRandomSongIndex() {

        max = songObjectList.size() - 1;
        min = 0;

        if(noSongHasBeenPlayedYet == true){

            int songIndex = randomUtil.nextInt((max - min) + 1) + min;

            SongObject songObject = songObjectList.get(songIndex);
            TryToPlaySong(songObject);
            noSongHasBeenPlayedYet = false;

            playHistory.add(songIndex);

        } else {

            int songIndex = randomUtil.nextInt((max - min) + 1) + min;

            // Do not a play a song in the song list that's already in the play history
            // untill that condition returns false

            while(songIndex == playHistory.get(playHistory.size()-1)){
                Log.v("RANDOM BBB","");
                songIndex = randomUtil.nextInt((max - min) + 1) + min;
            }

            SongObject songObject = songObjectList.get(songIndex);
            TryToPlaySong(songObject);
            playHistory.add(songIndex);

        }

    }

    public static void TryToPlaySong(SongObject songObject) {
        try {PlaySong(songObject);}
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void PlaySong(SongObject songObject) throws IllegalArgumentException, IllegalStateException, IOException {
        //String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(songObject.songPath); // songObject.data is a path
        mediaPlayer.prepare();
        mediaPlayer.start();

        //currentTitleView.setText(songObject.title);
        //currentArtistView.setText(songObject.artist);

        currentTitle = songObject.songTitle;
        currentArtist = songObject.artist;

        currentSongObect = songObject;

    }
}
