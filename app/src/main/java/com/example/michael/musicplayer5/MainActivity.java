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

    // TESTEST

    /**View References**/
    ListView listView; // Widgets cannot be instantiated until after setContent() is called
    static ToggleButton playButton;
    ToggleButton skipForwardsButton;
    ToggleButton skipBackwardsButton;
    ToggleButton shuffleButton;
    ToggleButton loopButton;

    /**Non View References**/
    ListAdapter adapter;
    int max;
    int min;
    int clickedSkipBackWardsButtonHowManyTimes = 0;

    /** Initializations **/
    static MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<Integer> playHistory = new ArrayList<>();
    ArrayList<com.example.michael.musicplayer5.SongObject> songList = new ArrayList<>();
    Random randomUtil = new Random();

    /** Boolean Initializations **/
    Boolean noSongHasBeenPlayedYet = true;
    Boolean shuffleOn = false;
    Boolean loopModeOn = false;
    static Boolean pressedPlay = false;

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

        /** mediaPlayer Listeners **/
        Listeners();
    }

    private void Listeners() {

        // If a song finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {

                if(loopModeOn == true){

                    mediaPlayer.start();
                } else {

                    PlayAndIndexASong();
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

        /** List View Listener **/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // arg2 is the position of the view which corresponds to a list array index
                // .get() method on this index will return a song object

                if (playButton.isChecked() == false) { // If set to play

                    playButton.setChecked(true); // set to pause
                }

                TryToPlaySong(songList.get(arg2).data);
                if (noSongHasBeenPlayedYet == true) {
                    noSongHasBeenPlayedYet = false;
                }

                // If list view items is clicked after user has clicked back tracked a number of times
                // than that many items should be deleted from the play history list
                // since a new play list will be branched from that point

                if (clickedSkipBackWardsButtonHowManyTimes > 0) {

                    int i;
                    int j=0;

                    for (i = clickedSkipBackWardsButtonHowManyTimes; i > 0; i--) {

                        playHistory.remove(playHistory.size() - 1); // Remove last element in the play history
                        j+=1;
                    }

                    clickedSkipBackWardsButtonHowManyTimes = clickedSkipBackWardsButtonHowManyTimes - j;
                }

                if(playHistory.size()>=1){
                    // Add the clicked song to the play list
                    if(songList.get(arg2) != songList.get(playHistory.get(playHistory.size()-1))){ // <---
                        playHistory.add(arg2);

                    }else{
                        // do nothing
                    }
                }else{

                    playHistory.add(arg2);
                }
            }
        });

        /** Play Button Listener **/
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(noSongHasBeenPlayedYet == true){

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

        /** Skip Forwards Listener **/
        skipForwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        /** Skip backwards Listener **/
        skipBackwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playHistory.size() == 0) { // If there are no songs in the play history

                    // Do nothing

                } else {

                    // If current position is greater 5 seconds, restart
                    if(mediaPlayer.getCurrentPosition() > 10000){

                        if(playHistory.size() > 1){

                            TryToPlaySong(songList.get(playHistory.get(playHistory.size()-1-clickedSkipBackWardsButtonHowManyTimes)).data); // Get the last song
                        } else {
                            Log.v("Replay","");
                            TryToPlaySong(songList.get(playHistory.get(0)).data); // Get first song if there is only one song
                            Log.v("Replay", "");
                        }

                    } else {

                        // Avoid out of bounds error with play history
                        if(playHistory.size() - 1 - clickedSkipBackWardsButtonHowManyTimes > 0){

                            Log.v("AAA ,",String.valueOf(playHistory.size() - 1));
                            Log.v("BBB ",String.valueOf(clickedSkipBackWardsButtonHowManyTimes));

                            clickedSkipBackWardsButtonHowManyTimes += 1;

                            int songIndex = playHistory.size() - 1;
                            songIndex = songIndex - clickedSkipBackWardsButtonHowManyTimes;

                            TryToPlaySong(songList.get(playHistory.get(songIndex)).data);
                        } else {
                            // do nothing
                            Log.v("CCC ","");
                        }
                    }
                }
            }
        });
    }

    public void PlayAndIndexASong() {



        if(clickedSkipBackWardsButtonHowManyTimes > 0){

            Log.v("Play history size: ",String.valueOf(playHistory.size()));
            Log.v("Clicked back button: ",String.valueOf(clickedSkipBackWardsButtonHowManyTimes));
            Log.v("Difference: ",String.valueOf(playHistory.size() - clickedSkipBackWardsButtonHowManyTimes));

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

    public void GetNextSongIndex(){



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

    public void GetRandomSongIndex() {

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

    private Cursor GetCursor() {
        Uri contentURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // Set URI

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
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

                if (mCursor.getString(1).equals("<unknown>")) {
                    songObject.artist = "Unknown Artist";
                }
                else {
                    songObject.artist = mCursor.getString(1);
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
