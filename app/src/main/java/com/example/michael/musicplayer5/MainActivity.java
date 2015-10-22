package com.example.michael.musicplayer5;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ToggleButton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    /**View References**/
    ListView listView; // Widgets cannot be instantiated until after setContent() is called
    ToggleButton playButton;
    ToggleButton skipForwardsButton;
    ToggleButton skipBackwardsButton;
    ToggleButton shuffleButton;
    ToggleButton loopButton;

    /**Non View References**/
    ListAdapter adapter;
    String song;
    int randomNum;
    int max;
    int min;
    int lastSongPlayedIndex;
    int nextSongToPlayIndex;

    /** Initializations **/
    static MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<com.example.michael.musicplayer5.SongObject> songList = new ArrayList<>();
    Random randomUtil = new Random();

    /** Boolean Initializations **/
    Boolean noSongHasBeenPlayedYet = true;
    Boolean shuffleOn = false;
    Boolean loopListOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Assign global view references **/
        playButton = (ToggleButton) findViewById(R.id.playButton);
        skipForwardsButton = (ToggleButton) findViewById(R.id.skipForwards);
        skipBackwardsButton = (ToggleButton) findViewById(R.id.skipBackwards);
        shuffleButton = (ToggleButton) findViewById(R.id.shuffle);
        loopButton = (ToggleButton) findViewById(R.id.loopList);
        listView = (ListView) findViewById(R.id.song_list); // Set's reference to view object

        /** Cursor **/
        Cursor mCursor = GetCursor(); // Points cursor to meta data
        MakeList(mCursor); // Creates list array of song objects

        /** Adapter **/
        adapter = new ListAdapter(this, R.layout.listview_item_row, songList); // Maps song objects to list view
        listView.setAdapter(adapter); // Set target list view for adapter logic

        /** mediaPlayer Interfaces **/
        Listeners();
    }

    private void Listeners() {

        // If a song finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                int songIndex;
                // If shuffle mode is on
                if (shuffleOn == true) {
                    // Play a random song but not the last song
                    songIndex = lastSongPlayedIndex;
                    while( songIndex == lastSongPlayedIndex){
                        songIndex = GetRandomSongIndex();
                    }
                    TryToPlaySong(songList.get(songIndex).data);
                    lastSongPlayedIndex = songIndex;
                    nextSongToPlayIndex = GetNextSongIndex(songIndex);
                }
                // If shuffle mode is off
                else {
                    // Play the next song in the list
                    songIndex = GetNextSongIndex(lastSongPlayedIndex);
                    TryToPlaySong(songList.get(songIndex).data);
                    lastSongPlayedIndex = songIndex;
                    nextSongToPlayIndex = GetNextSongIndex(lastSongPlayedIndex);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int songIndex = arg2;
                Log.v("TAG arg2: ", String.valueOf(arg2));
                TryToPlaySong(songList.get(songIndex).data);
                lastSongPlayedIndex = songIndex;
                if (playButton.isChecked() == false) {
                    playButton.setChecked(true);
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleOn == false){shuffleOn = true;}
                else{shuffleOn = false;}
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int songIndex;
                // If a song has not been played yet
                if (noSongHasBeenPlayedYet == true) {
                    // If shuffle mode is off -> play first song in the list
                    if (shuffleOn == false) {
                        songIndex = GetFirstSongIndex();
                        TryToPlaySong(songList.get(songIndex).data);
                        lastSongPlayedIndex = songIndex;
                        nextSongToPlayIndex = GetNextSongIndex(songIndex);
                        noSongHasBeenPlayedYet = false;
                    }
                    // If shuffle mode is on --> play a random song
                    else {
                        songIndex = GetRandomSongIndex();
                        TryToPlaySong(songList.get(songIndex).data);
                        lastSongPlayedIndex = songIndex;
                        nextSongToPlayIndex = GetNextSongIndex(songIndex);
                        noSongHasBeenPlayedYet = false;
                    }
                } else if (MainActivity.mediaPlayer.isPlaying() == true) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        });

        skipForwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        skipBackwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something
            }
        });
    }

    public int GetFirstSongIndex() {
        Log.v("TAG GetFirstSongIndex"," Method");
        int firstSongIndex;
        firstSongIndex = 0;
        return firstSongIndex;
    }
    /*
        public String GetNextSongIndex() {
            String nextSong;
            // get next song
            //return nextSong;
        }

        public String GetPreviousSong() {
            String previousSong;
            // get previous song
            //return previousSong;
        }
    */
    public int GetNextSongIndex(int lastSongIndex) {
        Log.v("TAG GetNextSongIndex"," Method");
        Log.v("TAG lastSongIndex: ",String.valueOf(lastSongIndex));
        int index = lastSongIndex;
        int nextSongIndex = index + 1;
        Log.v("TAG nextSongIndex: ",String.valueOf(nextSongIndex));
        return nextSongIndex;
    }

    public int GetRandomSongIndex() {
        Log.v("TAG GetRandomSongIndex"," Method");
        max = songList.size() - 1; // returns number of elements within array list minus 1
        min = 0;
        randomNum = randomUtil.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private Cursor GetCursor() {
        Uri contentURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // Set URI

        String[] projection = {
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";   // Set selection parameter for getContentResolver query
        String order = MediaStore.Audio.Media.TITLE + " ASC"; // Set sort order paramter
        final Cursor mCursor = getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        return mCursor;
    }

    private void MakeList(Cursor mCursor) {
        if (mCursor.moveToFirst()) {
            do {
                SongObject songObject = new SongObject();
                String[] albumID = {mCursor.getString(5)};
                songObject.albumArtURI = GetAlbumArtURI(albumID); // Return album art URI
                songObject.album = mCursor.getString(0);
                if (mCursor.getString(1).equals("<unknown>")) {songObject.artist = "Unknown Artist";}
                else {songObject.artist = mCursor.getString(1);}
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
