package com.example.michael.musicplayer5;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static MyPageAdapter pageAdapter;
    static List<Fragment> fragments;

    static ArrayList<com.example.michael.musicplayer5.SongObject> songObjectList;
    static ArrayList<String> artistList;
    static ArrayList<String> albumList;

    private Toolbar toolbar;
    private TabLayout tab;
    private ViewPager viewPager;
    private String tabTitle;

    static TextView currentTitleView;
    static TextView currentArtistView;

    /*
    static String currentTitle;
    static String currentArtist;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });**/

        songObjectList = new ArrayList<>();
        artistList = new ArrayList<>();
        albumList = new ArrayList<>();

        Cursor songListCursor = GetSongListCursor();
        MakeLists(songListCursor);

        fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(3);

        tabTitle = GetTabTitle();

        tab = (TabLayout) findViewById(R.id.tabs);
        tab.setupWithViewPager(viewPager);

        /** Buttons
        playButton = (ToggleButton) findViewById(R.id.playButton);
        skipForwardsButton = (ToggleButton) findViewById(R.id.skipForwards);
        skipBackwardsButton = (ToggleButton) findViewById(R.id.skipBackwards);
        shuffleButton = (ToggleButton) findViewById(R.id.shuffle);
        loopButton = (ToggleButton) findViewById(R.id.loopList); **/

        /*
        StaticMediaPlayer.SetButtonsMainActivity(
                (ToggleButton) findViewById(R.id.playButton),
                (ToggleButton) findViewById(R.id.skipForwards),
                (ToggleButton) findViewById(R.id.skipBackwards),
                (ToggleButton) findViewById(R.id.shuffle),
                (ToggleButton) findViewById(R.id.loopList),
                (TextView) findViewById(R.id.currentTitle),
                (TextView) findViewById(R.id.currentArtist),
                songObjectList
        );*/

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
        StaticMediaPlayer.SetButtonsMainActivity(
                (ToggleButton) findViewById(R.id.playButton),
                songObjectList
        );

        //Set play button listener
        StaticMediaPlayer.setPlayButtonListener();

        //Set play panel title and artist
        currentTitleView = (TextView) findViewById(R.id.currentTitle);
        currentArtistView = (TextView) findViewById(R.id.currentArtist);

        //currentTitleView.setText(songObject.title);
        //currentArtistView.setText(songObject.artist);

        TitlePanelClickListener();

        float elevation = 2;
        float density = getResources().getDisplayMetrics().density;

        View v = findViewById(R.id.shadowView);
        v.setBackgroundDrawable(new RoundRectDrawableWithShadow(
                getResources(), Color.BLACK, 0,
                elevation * density, ((elevation + 1) * density) + 1
        ));

        /**
        currentTitleView = (TextView) findViewById(R.id.currentTitle);
        currentArtistView = (TextView) findViewById(R.id.currentArtist);**/

    }

    public String GetTabTitle(){
        int currentTabIndex = viewPager.getCurrentItem();
        String title = "";
        if (currentTabIndex == 3) {
            title = "TRACKS";
        }
        return title;
    }

    public void TitlePanelClickListener(){

        LinearLayout TitlePanel = (LinearLayout) findViewById(R.id.activity_main_track_info);

        /** Title Listener **/
        TitlePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open the play panel

                Intent intent = new Intent(MainActivity.this, PlayPanelActivity.class);
                //intent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(intent);
                //overridePendingTransition(R.anim.slide_up, R.anim.dont_move);

            }
        });
    }

    private String GetAlbumArtURI(String[] albumID) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/Song.mp3");
        SingleMediaScanner singleMediaScanner = new SingleMediaScanner(this, file);

        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                albumID,
                null
        );

        if (mCursor.moveToFirst()) {
            Log.v("Here: ","here");
            return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        }
        else {
            mCursor.close();
            return null;
        }
    }

    private List<Fragment> getFragments() {

        // An empty array list
        List<Fragment> fList = new ArrayList<Fragment>();

        // Initialize and add fragment objects to the array list

        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));

        // Return the fragment object array list for adaption to the pager view
        return fList;
    }

    private Cursor GetSongListCursor() {

        // Set getContentResolver().query(contentURI, projection, selection, null, order) arguments

        Uri contentURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.ALBUM_ID};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String order = MediaStore.Audio.Media.TITLE + " ASC";

        // Uri maps to the table in the provider named table_name.
        // projection is an array of columns that should be included for each row retrieved.
        // selection specifies the criteria for selecting rows.
        // selectionArgs is null
        // sortOrder specifies the order in which rows appear in the returned Cursor.

        // Initialize a local cursor object with a query and return the cursor object
        final Cursor mCursor = getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        return mCursor;
    }

    private void MakeLists(Cursor mCursor) {

        try{

            if (mCursor.moveToFirst()) {

                do {

                    // Initialize songObject
                    SongObject songObject = new SongObject();

                    // Add albumURI
                    String[] albumID = {mCursor.getString(5)};
                    songObject.albumArtURI = GetAlbumArtURI(albumID);

                    // Add album
                    String album = mCursor.getString(0);
                    songObject.album = album;
                    if(!albumList.contains(album)){albumList.add(album);} // Add album to albumList

                    // Add artist
                    if (mCursor.getString(1).equals("<unknown>")) {

                        String artist = "Unknown Artist";
                        songObject.artist = artist;

                        if(!artistList.contains(artist)){artistList.add(artist);} // Add artist to artistList
                    }
                    else {

                        String artist = mCursor.getString(1);
                        songObject.artist = artist;

                        if(!artistList.contains(artist)){artistList.add(artist);} // Add artist to artistList
                    }


                    songObject.title = mCursor.getString(2);
                    songObject.data = mCursor.getString(3);
                    songObject.duration = mCursor.getString(4);

                    songObjectList.add(songObject);

                } while (mCursor.moveToNext());
            }

        } finally {

            mCursor.close();

        }
    }


    /**
     private void SetBackground(){
     // so the albumList is an existing global variable
     // return a random index from GetRandomSongIndex

     ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
     //ImageView backgroundView = (ImageView) findViewById(R.id.appbackground);

     max = songObjectList.size() - 1;
     min = 0;
     int songIndex;
     Bitmap bitMap;

     //pager.setBackgroundColor(Color.BLACK);

     // Need to change this so the loop doesn't continue forever if there are no album uris
     do {
     songIndex = randomUtil.nextInt((max - min) + 1) + min;
     bitMap = BitmapFactory.decodeFile(songObjectList.get(songIndex).albumArtURI); // Create bitmap
     } while (songObjectList.get(songIndex).albumArtURI == null);


     ScreenDimensions screenDimensions = new ScreenDimensions(this);
     int fromX = 0;
     int negativeMargin = bitMap.getWidth() - screenDimensions.width;

     //Set Negative Margins for background
     //ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) backgroundView.getLayoutParams();
     //p.setMargins(0, 0, negativeMargin, 0); // p.setMargins(l, t, r, b);
     //backgroundView.requestLayout();

     //Set Bit Map to Monocrhome
     BitMapToGrayScale converter = new BitMapToGrayScale();
     bitMap = converter.ToGrayScale(bitMap);

     //Set Bit Map As Background
     ResizeBitMap resizeBitMap = new ResizeBitMap(this);
     bitMap = resizeBitMap.resize(bitMap);
     Drawable drawable = new BitmapDrawable(getResources(), bitMap); // Convert bitmap to drawable
     //pager.setBackgroundDrawable(drawable);

     }**/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {return true;}
        return super.onOptionsItemSelected(item);
    }
}
