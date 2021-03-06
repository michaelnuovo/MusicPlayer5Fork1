package com.example.michael.musicplayer5;

import android.media.MediaPlayer;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by michael on 11/19/15.
 */

// test

public final class StaticMusicPlayer {

    /** Fields **/

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public static ToggleButton playButton = null;

    private static ToggleButton loopButton = null;
    private static Button skipForwardsButton = null;
    private static Button skipBackwardsButton = null;
    private static Button normalPlayButton = null;

    static ArrayList<SongObject> songObjectList = null;
    public static ArrayList<SongObject> playList = new ArrayList<>();

    static boolean shuffleOn = false;
    static boolean loopModeOn = false; // Right now loop mode on means replay the song current song
    static boolean pressedPlay = false; // Flag used for pausing and resuming music; default value is false

    static boolean noSongHasBeenPlayedYet = true; // default is true; gets set to false in playSong() method

    static int currentIndex = 0; // Current index of song being played (zero by default)

    private static Button shuffleButton = null;
    private static ArrayList<SongObject> shuffledList = null;

    private static ViewPager viewPager;

    static public boolean musicIsPlaying = false; // Variable sets the play button toggle mode

    static public boolean isPaused = false;




    /** Empty Constructor **/

    private StaticMusicPlayer(){

        // Empty private constructor
    }

    /** Setters **/

    public static void setList(ArrayList<SongObject> list){

        songObjectList = list;
    }

    public static void setPlayButton(ToggleButton btn){

        playButton = btn;
    }

    public static void setNormalPlayButton(Button btn){
        normalPlayButton = btn;
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

    public static void setShuffleButton(Button btn){

        shuffleButton = btn;
    }

    public static void setShuffleList(ArrayList<SongObject> arrayList){

        shuffledList = arrayList;
    }

    public static void setViewPager(ViewPager vp){

        viewPager = vp;
    }




    public static void setSongCompletionListener(){

        // If a song finishes playing
        mediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {

            public void onCompletion(android.media.MediaPlayer mp) {

                // if a song finishes we need to set musicIsPlaying to false for the toggle button state
                musicIsPlaying = false;

                // we also to start playing the next song if there is one
                if (currentIndex != songObjectList.size() - 1) {
                    tryToPlaySong(songObjectList.get(currentIndex + 1));
                }
            }
        });

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

    static public void togglePlaybutton(){

         ActivityMain.playButton.setChecked(!isPaused); //always want toggle to be opposite of pause state
         if(ActivityArtist.playButton != null){
             ActivityArtist.playButton.setChecked(!isPaused);
             Log.v("TAG", "ActivityArtist.playButton is not null ");}
        if(ActivityAlbum.playButton != null){
            ActivityAlbum.playButton.setChecked(!isPaused);
            Log.v("TAG", "ActivityArtist.playButton is not null ");}

    }

    public static void setNormalPlayButtonListener(){
        normalPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG", "clicked fb");


                if(ActivityPlayPanel.playPager != null) {
                    tryToPlaySong(songObjectList.get(ActivityPlayPanel.playPager.getCurrentItem()));

                }
            }
        });
    }

    static public void togglePauseState(){

        if(isPaused){mediaPlayer.start();isPaused=false;}
        else{mediaPlayer.pause();isPaused=true;}
        togglePlaybutton();
    }

    /** Listeners**/
    public static void tryToPlaySong(final SongObject songObject) {

        noSongHasBeenPlayedYet = false;
        currentIndex = songObjectList.indexOf(songObject);
        musicIsPlaying = true;
        isPaused = false;

        togglePlaybutton();
        //ActivityMain.footerPager.invalidate();
        //ActivityMain.footerPager.setCurrentItem(StaticMusicPlayer.currentIndex); //refresh footer pager in main activity
        ActivityMain.footerPager.setAdapter(new PageAdapterFooter(ActivityMain.getAppContext()));
        ActivityMain.footerPager.setCurrentItem(currentIndex);

        if(ActivityArtist.footerPager != null){ActivityArtist.footerPager.setAdapter(new PageAdapterFooter(ActivityMain.getAppContext()));}
        if(ActivityArtist.footerPager != null){ActivityArtist.footerPager.setCurrentItem(currentIndex);}

        if(ActivityAlbum.footerPager != null){ActivityAlbum.footerPager.setAdapter(new PageAdapterFooter(ActivityMain.getAppContext()));}
        if(ActivityAlbum.footerPager != null){ActivityAlbum.footerPager.setCurrentItem(currentIndex);}

        if(ActivityPlayPanel.playPager != null){ActivityPlayPanel.playPager.setCurrentItem(currentIndex);}

        //footer_song_info.setText(StaticMusicPlayer.songObjectList.get(StaticMusicPlayer.currentIndex).songTitle);
        //footer_song_artist.setText(StaticMusicPlayer.songObjectList.get(StaticMusicPlayer.currentIndex).artist);

        new Thread() {
            public void run() {
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
        }.start();
    }

    private static void playSong(SongObject songObject) throws IllegalArgumentException, IllegalStateException, IOException {

        mediaPlayer.reset();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(songObject.songPath);
        mediaPlayer.prepare();
        mediaPlayer.start();

        setSongCompletionListener();


    }

    public static void setSkipForwardsListener() {

        /** Skip Forwards Listener **/
        skipForwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG","clicked fb");

                if(ActivityPlayPanel.playPager != null){ActivityPlayPanel.playPager.setCurrentItem(ActivityPlayPanel.playPager.getCurrentItem()+1);}

                /**
                if (currentIndex == songObjectList.size() - 1) {

                    tryToPlaySong(songObjectList.get(0)); // play the zero index song
                } else {
                    tryToPlaySong(songObjectList.get(currentIndex + 1)); // play the next song
                }***/

            }
        });
    }

    public static void setSkipBackwardsListener() {

        /** Skip backwards Listener **/
        skipBackwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ", "back button clicked");

                if(ActivityPlayPanel.playPager != null){ActivityPlayPanel.playPager.setCurrentItem(ActivityPlayPanel.playPager.getCurrentItem()-1);}

                /*
                if (currentIndex == 0) {

                    //SongObject so = songObjectList.get(songObjectList.size()-1); // get the last element
                    //songObjectList.remove(songObjectList.size()-1); // remove the last element
                    //songObjectList.add(0, so); // add last element to beginning of list

                    tryToPlaySong(songObjectList.get(0));

                } else {

                    tryToPlaySong(songObjectList.get(currentIndex - 1)); // play the preceding song
                }*/
            }
        });
    }

    public static void setPlayButtonListener(){

        /** Play Button Listener **/
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG: ", "play button clicked");

                // So if no song has been played yet, and the user clicks on the play
                // button from the main activity, this means that we need to play the
                // first song in the alphabetical song list, and since that song has already
                // been passed to the music player by default via the main activity,
                // all we need to do is playing the first song in the music player song list
                // i.e. play the first song if noSongHasBeenPlayedYet = true
                // otherwise we will have to test if the current song is pause or not.
                // remember that noSongHasBeenPlayedYet is set to false in .playSong()
                // so no need to do it here.

                if(noSongHasBeenPlayedYet == true) {

                    Log.v("TAG try to play :", String.valueOf(songObjectList.get(0)));
                    tryToPlaySong(songObjectList.get(0));

                } else {

                    Log.v("TAG","here");
                    // So if we push play, then

                    if(isPaused == true){ //

                        Log.v("TAG","No longer pause");
                        isPaused = false;

                        mediaPlayer.start();
                        musicIsPlaying = true;
                        StaticMusicPlayer.togglePlaybutton();

                    }else{                   // pause
                        Log.v("TAG","Paused now");
                        isPaused = true;

                        mediaPlayer.pause();
                        musicIsPlaying = false;
                        StaticMusicPlayer.togglePlaybutton();
                    }
                }
            }
        });
    }

    public static void setShuffleButtonListener(){

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Always make sure play button toggle state is set to false

                pressedPlay = false;

                // So here, when the shuffle button is pressed from the main activity,
                // we want the play panel activity to open, and we want to
                // play the first song of a shuffled played list.
                // First we need to take the songObjectList, and we need to shuffle it
                // (which we do from the main activity).
                // Second we need to pass the shuffled list to static music player
                // (which we do here).
                // Third we need to call the static music player play method on the first song
                // in the shuffled list (which we do here).

                setList(shuffledList);
                tryToPlaySong(shuffledList.get(0));

                // We want to reshuffle the list again every time the user clicks on the shuffle button
                // since we want to save that list for the next time the user click on the button again.

                Collections.shuffle(shuffledList);
            }
        });
    }

    public static void SetViewPagerListener(){

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {

                if(!isPaused){
                    tryToPlaySong(songObjectList.get(position));
                }
            }
        });
    }
}

