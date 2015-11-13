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

    static MyPageAdapterMain pageAdapter;
    static List<Fragment> fragments;

    static ArrayList<SongObject> songObjectList;
    static ArrayList<AlbumObject> albumObjectList = new ArrayList<>();
    static ArrayList<ArtistObject> artistObjectList = new ArrayList<>();

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

        Cursor songListCursor = GetSongListCursor();
        MakeLists(songListCursor);

        fragments = getFragments();
        pageAdapter = new MyPageAdapterMain(getSupportFragmentManager(), fragments);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(3);

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
    private List<Fragment> getFragments() {

        // An empty array list
        List<Fragment> fList = new ArrayList<Fragment>();

        // Initialize and add fragment objects to the array list

        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));  // 3 - TRACKS
        fList.add(MyFragmentArtists.newInstance(artistObjectList)); // 4 - ARTISTS

        Log.v("TAG:","Adding fragments");

        fList.add(MyFragmentAlbums.newInstance(albumObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));

        // Return the fragment object array list for adaption to the pager view
        return fList;
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

                    // add albumTitle
                    String albumTitle = mCursor.getString(0);
                    songObject.albumTitle = albumTitle;

                    // Add artist or add "Unknown Artist"
                    String artist = mCursor.getString(1);
                    if (artist.equals("<unknown>")) {

                        artist = "Unknown Artist";
                        songObject.artist = artist;
                    }
                    else {

                        artist = mCursor.getString(1);
                        songObject.artist = artist;
                    }

                    String songTitle = mCursor.getString(2);
                    songObject.songTitle = songTitle;

                    String songPath = mCursor.getString(3);
                    songObject.songPath = songPath;

                    String songDuration = mCursor.getString(4);
                    songObject.songDuration = songDuration;

                    songObjectList.add(songObject);

                    String albumArtist = songObject.artist; // can an album have more than one artist? yes...
                    String albumArtURI = songObject.albumArtURI;

                    // if albumObjectList is empty, add a new album object
                    // then check if there is an albumObject in the list where albumObject.albumTitle = songObject.albumTitle
                    // if yes, add the songObject to the existing albumObjects albumObject.songObjectList
                    // if not, create a new albumObject, add the songObject to albumObject.songObjectList
                    // we also need to increment the albumTrackCount in the process

                    if(albumObjectList.isEmpty()){
                        albumObjectList.add(new AlbumObject(albumTitle,albumArtist,albumArtURI,songObject));
                    } else {

                        boolean bool = false;
                        for(int i = 0; i < albumObjectList.size(); i++) {

                            if(albumObjectList.get(i).albumTitle.equals(albumTitle)) {
                                albumObjectList.get(i).songObjectList.add(songObject);
                                albumObjectList.get(i).albumTrackCount += 1;

                                bool = true;
                            }
                        }

                        if(bool == false){
                            AlbumObject newAlbumObject = new AlbumObject(albumTitle,albumArtist,albumArtURI,songObject);
                            newAlbumObject.albumTrackCount +=1;
                            albumObjectList.add(newAlbumObject);
                        }
                    }

                    // Do the same thing for the artistObjectList

                    if(artistObjectList.isEmpty()){
                        artistObjectList.add(new ArtistObject(songObject));
                    } else {

                        boolean bool = false;
                        for(int i = 0; i < artistObjectList.size(); i++) {

                            if(artistObjectList.get(i).artistName.equals(artist)) {
                                artistObjectList.get(i).songObjectList.add(songObject);
                                artistObjectList.get(i).artistTrackCount += 1;

                                bool = true;

                            }

                        }

                        if(bool == false){
                            ArtistObject newArtistObject = new ArtistObject(songObject);
                            newArtistObject.artistTrackCount += 1;
                            artistObjectList.add(newArtistObject);
                        }
                    }



                } while (mCursor.moveToNext());
            }

        } finally {

            // Find unique number of instancse of albums
            //artistObjectList.setAlbumsCounter();

            // Close cursor
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
