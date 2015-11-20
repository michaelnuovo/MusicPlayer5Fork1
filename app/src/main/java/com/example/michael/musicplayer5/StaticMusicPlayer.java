package com.example.michael.musicplayer5;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by michael on 11/19/15.
 */

// test

public final class StaticMusicPlayer {

    /** Fields **/

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    private static ToggleButton playButton = null;
    private static ToggleButton shuffleButton = null;
    private static ToggleButton loopButton = null;
    private static Button skipForwardsButton = null;
    private static Button skipBackwardsButton = null;

    private static ArrayList<SongObject> songObjectList = null;
    public static ArrayList<SongObject> playList = new ArrayList<>();

    static boolean shuffleOn = false;
    static boolean loopModeOn = false; // Right now loop mode on means replay the song current song
    static boolean pressedPlay = false; // Flag used for pausing and resuming music; default value is false

    static boolean noSongHasBeenPlayedYet = true; // default is true; gets set to false in playSong() method

    static int currentIndex = 0; // Current index of song being played (zero by default)

    /** Empty Constructor **/

    private StaticMusicPlayer(){

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

        skipForwardsButton = btn;
    }

    public static void skipLeftButton(Button btn){

        skipBackwardsButton = btn;
    }

    /** Get song **/

    public static void tryToPlayNextSong(){

        // if shuffle mode is on do this,

        // else do this.
        // Now here we want to get a song object from songObjectList
        // that isn't in the playList.
        // we want to play it and add it to the playList.
        // The song we will add will be the one in the song object list that comes after the last one.
        // There will already be at least one song in the play list by this point.

        tryToPlaySong(songObjectList.get(songObjectList.indexOf(playList.get(playList.size() - 1)) + 1));

    }

    public static void tryToplayRandomSong(){

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

        Log.v("TAG @#$$%#$%$ ", String.valueOf(songObject.songTitle));

        mediaPlayer.reset();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(songObject.songPath);
        mediaPlayer.prepare();
        mediaPlayer.start();

        noSongHasBeenPlayedYet = false;

    }



    public static void setSkipForwardsListener() {

        /** Skip Forwards Listener **/
        skipForwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ", "forwards button clicked");

                // Play the next song in the list,
                // but we need to also make sure there is no out of bounds error.
                // If the next index would be out of bounds, then start at the first index, index 0,
                // although the play list will stack on itself and we wont actually need out of bound protection,
                // but let's include out of bound protection anyways.
                // We also have to page the pager, though I would like this functionality outside this class,
                // but apparently I can't have have more than two listeners at once, and I don't have the technical
                // know how to get around this restriction.
                if (currentIndex == songObjectList.size() - 1) {

                    tryToPlaySong(songObjectList.get(0)); // play the zero index song
                } else {
                    tryToPlaySong(songObjectList.get(currentIndex + 1)); // play the next song
                }

                //PlayPanelActivity.playPager.setCurrentItem(PlayPanelActivity.position + 1, true); // Pager will page on click
            }
        });
    }

    public static void setSkipBackwardsListener() {

        /** Skip backwards Listener **/
        skipBackwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ", "back button clicked");

                // Play the previous song in the list,
                // but we need to also make sure there is no out of bounds error.
                // However, rather than checking if the previous index would be less than zero,
                // we are just going to take the last song object from the list
                // and move it to the front, this way the shuffle mechanism
                // will be easier to program

                // Actually move the last element in the list to the beginning won't work
                // with the view pager which is only compatible with adding views to the right
                // so we will actually want to protect the program from an out of bounds error
                // by playing the last song in the list, and we can even position the pager
                // at the end of the list with set current item method.

                // I think it will be better to random the index being played rather than physically
                // and re-positioning the view pager as i don't think the pager likes it when
                // i rotate the list items around

                //

                if (currentIndex == 0) {

                    //SongObject so = songObjectList.get(songObjectList.size()-1); // get the last element
                    //songObjectList.remove(songObjectList.size()-1); // remove the last element
                    //songObjectList.add(0, so); // add last element to beginning of list

                    tryToPlaySong(songObjectList.get(0));

                } else {

                    tryToPlaySong(songObjectList.get(currentIndex - 1)); // play the preceding song
                }

                //PlayPanelActivity.playPager.setCurrentItem(PlayPanelActivity.position - 1, true); // Pager will page on click
                //PlayPanelActivity.position -= 1;

            }
        });
    }

    public static void setPlayButtonListener(){

        /** Play Button Listener **/
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // So if no song has been played yet, and the user clicks on the play
                // button from the main activity, this means that we need to play the
                // first song in the alphabetical song list, and since that song has already
                // been passed to the music player by default via the main activity,
                // all we need to do is playing the first song in the music player song list
                // i.e. play the first song if noSongHasBeenPlayedYet = true
                // otherwise we will have to test if the current song is pause or not.
                // remember that noSongHasBeenPlayedYet is set to false in .playSong()
                // so no need to do it here.

                tryToPlaySong(songObjectList.get(0));

                /*
                if(noSongHasBeenPlayedYet == true) {

                    tryToPlaySong(songObjectList.get(0));

                } else {

                    // So if we push play, then

                    if(pressedPlay == true){ //
                        pressedPlay = false;
                        mediaPlayer.start();

                    }else{                   // pause
                        pressedPlay = true;
                        mediaPlayer.pause();
                    }
                }*/
            }
        });
    }
}

