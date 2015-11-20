package com.example.michael.musicplayer5;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by michael on 11/19/15.
 */

// test

public final class StaticMediaPlayer {

    /** Fields **/

    private static MediaPlayer mediaPlayer = new MediaPlayer();

    private static ToggleButton playButton = null;
    private static ToggleButton shuffleButton = null;
    private static ToggleButton loopButton = null;
    private static Button skipRightButton = null;
    private static Button skipLeftButton = null;

    private static ArrayList<SongObject> songObjectList = null;
    private static ArrayList<SongObject> shuffledList = null;

    static Boolean shuffleOn = false;
    static Boolean loopModeOn = false; // Right now loop mode on means replay the song current song
    static Boolean pressedPlay = false;

    static int currentIndex; // Current index of song being played

    /** Empty Constructor **/

    private StaticMediaPlayer(){

        // Empty private constructor
    }

    /** Setters **/

    public static void setSongObjectList(ArrayList<SongObject> list){

        songObjectList = list;
    }

    public static void setPlayButton(ToggleButton btn){

        playButton = btn;
    }

    public static void setShuffleButton(ToggleButton btn){

        shuffleButton = btn;
    }

    public static void setLoopButton(ToggleButton btn){

        loopButton = btn;
    }

    public static void skipRightButton(Button btn){

        skipRightButton = btn;
    }

    public static void skipLeftButton(Button btn){

        skipLeftButton = btn;
    }

    /** Listeners**/

    public static void tryToPlaySong(SongObject songObject) {
        try {
            playSong(songObject);
        }
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

    private static void playSong(SongObject songObject) throws IllegalArgumentException, IllegalStateException, IOException {

        mediaPlayer.reset();
        mediaPlayer.setDataSource(songObject.songPath);
        mediaPlayer.prepare();
        mediaPlayer.start();

        currentIndex = songObjectList.indexOf(songObject);
    }

    public static void setSkipForwardsListener() {

        /** Skip Forwards Listener **/
        skipRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ", "forwards button clicked");

                // Play the next song in the list if shuffle is off
                // but we need to also make sure there is no out of bounds error
                // if the next index would be out of bounds, then start at the first index, index 0
                // if shuffle is on then we need to shuffle the list
                if (currentIndex == songObjectList.size() - 1) {

                    tryToPlaySong(songObjectList.get(0)); // play the zero index song
                } else {
                    tryToPlaySong(songObjectList.get(currentIndex+ 1)); // play the next song
                }

            }
        });
    }
}

