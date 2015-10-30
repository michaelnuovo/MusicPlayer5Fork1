package com.example.michael.musicplayer5;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    /** View Pager References **/
    static MyPageAdapter pageAdapter;
    static List<Fragment> fragments;

    /** List References **/
    static ArrayList<com.example.michael.musicplayer5.SongObject> songList;
    static ArrayList<String> artistList;
    static ArrayList<String> albumList;

    /** Initializations **/
    static MediaPlayer mediaPlayer = new MediaPlayer();
    static ArrayList<Integer> playHistory = new ArrayList<>();
    static Random randomUtil = new Random();

    /** Boolean Initializations **/
    static Boolean noSongHasBeenPlayedYet = true;
    static Boolean shuffleOn = false;
    static Boolean loopModeOn = false;
    static Boolean pressedPlay = false;

    /** Other **/
    static ListAdapter adapter;
    static int max;
    static int min;
    static int clickedSkipBackWardsButtonHowManyTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** List Initializations **/
        songList = new ArrayList<>();
        artistList = new ArrayList<>();
        albumList = new ArrayList<>();

        Cursor songListCursor = GetSongListCursor();
        MakeLists(songListCursor);

        /** Fragments & Pager  **/
        fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        //viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(pageAdapter);

        /** List Adapter **/
        //adapter = new ListAdapter(this, R.layout.list_view_item_title, songList);
        //listView.setAdapter(adapter);

    }

    private List<Fragment> getFragments() {

        // An empty array list
        List<Fragment> fList = new ArrayList<Fragment>();

        // Initialize and add fragment objects to the array list

        fList.add(MyFragment.newInstance(songList));
        fList.add(MyFragment.newInstance(songList));
        fList.add(MyFragment.newInstance(songList));

        // Return the fragment object array list for adaption to the pager view
        return fList;
    }

    public static void PlayAndIndexASong() {

        if(clickedSkipBackWardsButtonHowManyTimes > 0){

            Log.v("Play history size: ",String.valueOf(playHistory.size()));
            Log.v("Clicked back button: ",String.valueOf(clickedSkipBackWardsButtonHowManyTimes));
            Log.v("Difference: ", String.valueOf(playHistory.size() - clickedSkipBackWardsButtonHowManyTimes));

            int songIndex = playHistory.size() - 1 - clickedSkipBackWardsButtonHowManyTimes + 1;
            TryToPlaySong(songList.get(playHistory.get(songIndex)).data);
            clickedSkipBackWardsButtonHowManyTimes -=1 ;

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

            TryToPlaySong(songList.get(0).data);
            noSongHasBeenPlayedYet = false;

            playHistory.add(0);


        } else {
            Log.v("HELLO WORLD","HELLO WORLD");
            // To avoid out of bounds error
            if(playHistory.get(playHistory.size()-1) // returns an index
                    == songList.size()-1) // the last index
            { // <--

                Log.v("TAG","HERE");

                TryToPlaySong(songList.get(0).data);


                playHistory.add(0);

            } else {

                Log.v("TAG","HERE AGAIN");

                int songIndex = playHistory.get(playHistory.size() - 1); // get the last song index in the play history
                songIndex = songIndex + 1; // add one to it

                TryToPlaySong(songList.get(songIndex).data); // play it

                playHistory.add(songIndex); // add it to the play history

            }
        }
    }

    public static void GetRandomSongIndex() {

        max = songList.size() - 1;
        min = 0;

        if(noSongHasBeenPlayedYet == true){

            int songIndex = randomUtil.nextInt((max - min) + 1) + min;

            TryToPlaySong(songList.get(songIndex).data);
            noSongHasBeenPlayedYet = false;

            playHistory.add(songIndex);

        } else {

            int songIndex = randomUtil.nextInt((max - min) + 1) + min;
            Log.v("RANDOM AAA","");
            while(songIndex == playHistory.get(playHistory.size()-1)){
                Log.v("RANDOM BBB","");
                songIndex = randomUtil.nextInt((max - min) + 1) + min;
            }
            Log.v("RANDOM CCC","");
            TryToPlaySong(songList.get(songIndex).data);
            playHistory.add(songIndex);

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
                if(!artistList.contains(album)){artistList.add(album);} // Add album to albumList

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

                songList.add(songObject);



            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }

    public static void TryToPlaySong(String string) {
        try {PlaySong(string);}
        catch (IllegalArgumentException e) {e.printStackTrace();}
        catch (IllegalStateException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }

    private static void PlaySong(String path) throws IllegalArgumentException, IllegalStateException, IOException {
        //String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepare();
        mediaPlayer.start();

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
        if (mCursor.moveToFirst()) {return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));}
        else {return null;}
    }

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
