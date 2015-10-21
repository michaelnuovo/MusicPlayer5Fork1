package com.example.michael.musicplayer5;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ToggleButton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**View Group References**/
    ListView listView; // Widgets cannot be instantiated until after setContent() is called
    ToggleButton toggleButton;

    /**Other References**/
    ListAdapter adapter;
    String song;

    /** Initializations **/
    static MediaPlayer mMediaPlayer = new MediaPlayer();
    Boolean songHasBeenPlayedAtLeastOnce = true;
    ArrayList<com.example.michael.musicplayer5.SongObject> songObectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** View Groups **/
        toggleButton = (ToggleButton) findViewById(R.id.playButton);
        listView = (ListView) findViewById(R.id.song_list); // Set's reference to view object

        /** Cursor **/
        Cursor mCursor = GetCursor(); // Points cursor to meta data
        MakeList(mCursor); // Creates list array of song objects

        /** Adapter **/
        adapter = new ListAdapter(this, R.layout.listview_item_row, songObectList); // Maps song objects to list view
        listView.setAdapter(adapter); // Set target list view for adapter logic

        /** mediaPlayer Interfaces **/
        ClickListenerListItem(); // Listens for list item click events
        ClickListenerPlayButton();
    }

    private void ClickListenerListItem() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (toggleButton.isChecked() == false) {toggleButton.setChecked(true);}
                TrySong(songObectList.get(arg2).data);
            }
        });
    }

    public void ClickListenerPlayButton() {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                song = SelectSong();
                if (songHasBeenPlayedAtLeastOnce == false) {MainActivity.TrySong(song);songHasBeenPlayedAtLeastOnce = true;}
                else if (MainActivity.mMediaPlayer.isPlaying() == true) {MainActivity.mMediaPlayer.pause();}
                else {MainActivity.mMediaPlayer.start();}
            }
        });
    }

    public String SelectSong() {
        song = songObjectList.get(0).data;
        return song;
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
                songObectList.add(songObject);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }

    public static void TrySong(String string) {
        try {PlaySong(string);}
        catch (IllegalArgumentException e) {e.printStackTrace();}
        catch (IllegalStateException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }

    private static void PlaySong(String path) throws IllegalArgumentException, IllegalStateException, IOException {
        //String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
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
