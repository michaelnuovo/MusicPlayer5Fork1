package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import org.apache.commons.io.FileUtils;

public class ActivityArtists extends AppCompatActivity {


    //testing

    final static ArrayList<SongObject> mainList = null; // we need this variables to be global so that it can be referenced from other activities
    private static Context ctx;
    public static Context getAppContext() {
        return ctx;
    }
    private Activity activity;
    private ArrayList<SongObject> requestList = new ArrayList<>();
    String targetValue;
    static ViewPager footerPager;
    TextView footer_song_info;
    TextView footer_song_artist;



    final static ArrayList<AlbumObject> albumObjectList = new ArrayList<>();

    /** Print albums in albums list**/
    public void printAlbums(){
        for(int i=0;i<albumObjectList.size();i++){
            Log.v("TAG","albumObjectList.get(i).albumTitle is "+albumObjectList.get(i).albumTitle);
            Log.v("TAG","albumObjectList.get(i).albumTitle uri is "+albumObjectList.get(i).albumArtURI);
        }
    }

    @Override
    public void onResume(){

        /** Set play button and play button listener **/
        StaticMusicPlayer.setPlayButton((ToggleButton) findViewById(R.id.main_playButton));
        StaticMusicPlayer.setPlayButtonListener();
        super.onResume();

        /** refresh album pager **/
        footerPager.setCurrentItem(StaticMusicPlayer.currentIndex);

        //footer_song_info.setText(StaticMusicPlayer.songObjectList.get(StaticMusicPlayer.currentIndex).songTitle);
        //footer_song_artist.setText(StaticMusicPlayer.songObjectList.get(StaticMusicPlayer.currentIndex).artist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         String encStr = "not encoded";
         try {
         encStr = URLEncoder.encode("ASd asd", "UTF-8");
         } catch (UnsupportedEncodingException e) {
         Log.v("TAG","stack trace");
         e.printStackTrace();
         }
         Log.v("TAG","this thing here is : "+encStr);
         **/

        /** Set global context variables **/
        ctx = getApplicationContext();
        activity = this;

        /** Make song object list, album object list, and artist object list **/
        final ArrayList<SongObject> songObjectList = new ArrayList<>();
        final ArrayList<SongObject> songObjectList_shuffled = new ArrayList<>();
        final ArrayList<ArtistObject> artistObjectList = new ArrayList<>();
        //mainList = songObjectList;
        //StaticMusicPlayer.setList(mainList);

        /** Populate lists **/
        //scanMedia();
        Cursor songListCursor = GetSongListCursor();
        MakeLists(songListCursor, songObjectList, songObjectList_shuffled, albumObjectList, artistObjectList);
        Collections.shuffle(songObjectList_shuffled);

        /** Restoring original artworks**/
        //Log.v("TAG", "album object list size is : " + albumObjectList.size());
        //for(int i=0;i<albumObjectList.size();i++){
        //    Log.v("TAG", "album title is : " + albumObjectList.get(i).albumTitle);
        //}

        /** Make network requests chain starting with last fm**/
        MediaStoreInterface mint = new MediaStoreInterface(ctx);
        //mint.clearFolder("myalbumart");

        AlbumArt aa = new AlbumArt(this,albumObjectList);
        //aa.resetPaths(); // set to album path to null if there are no images

        aa.dumpAlbumColumns();

        LastFmAlbumLookup lf = new LastFmAlbumLookup(this,albumObjectList);
        //lf.makeRequest();

        //SpotifySolo spy = new SpotifySolo(this,albumObjectList);
        //spy.makeRequest();

        //ItunesSolo it = new ItunesSolo(this,albumObjectList);
        //it.makeRequest();

        //AlbumArt aa = new AlbumArt(this,albumObjectList);
        //aa.resetPaths(); // set to album path to null if there are no images
        //aa.setAllPathsToNull();
        //printAlbums();

        /** Target value **/
        targetValue = "null";

        /**  Logs **/
        //Log.v("TAG","value of activity is "+activity);

        /** Initialize static music player **/
        StaticMusicPlayer.setList(songObjectList);

        /** Set adapter for main activity **/
        List<Fragment> fragments = getFragments(songObjectList, artistObjectList, albumObjectList);
        PageAdapterMainActivity pageAdapter = new PageAdapterMainActivity(getSupportFragmentManager(), fragments);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(3);

        /** Set tab strip for main activity**/
        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setupWithViewPager(viewPager);

        /** Set play button and play button listener **/
        StaticMusicPlayer.setPlayButton((ToggleButton) findViewById(R.id.main_playButton));
        StaticMusicPlayer.setPlayButtonListener();

        /** Set shuffle button and shuffle button listener **/
        StaticMusicPlayer.setShuffleList(songObjectList_shuffled);
        StaticMusicPlayer.setShuffleButton((Button) findViewById(R.id.shuffleButton));
        StaticMusicPlayer.setShuffleButtonListener();

        mainFooterListener(songObjectList);

        /** pager for albums **/
        footerPager = (ViewPager) findViewById(R.id.footerPager);
        footerPager.setAdapter(new PageAdapterFooter(this));
        footerPager.setCurrentItem(StaticMusicPlayer.currentIndex);


        /** set footer song info **/
        //footer_song_info = (TextView) findViewById(R.id.footer_song_title);
        //footer_song_info.setText(StaticMusicPlayer.songObjectList.get(StaticMusicPlayer.currentIndex).songTitle);
        //footer_song_artist = (TextView) findViewById(R.id.footer_song_artist);
        //footer_song_artist.setText(StaticMusicPlayer.songObjectList.get(StaticMusicPlayer.currentIndex).artist);


        /** Draw Footer Shadow**/
        float elevation = 2;
        float density = getResources().getDisplayMetrics().density;

        View v = findViewById(R.id.shadowView);
        v.setBackgroundDrawable(new RoundRectDrawableWithShadow(
                getResources(), Color.BLACK, 0,
                elevation * density, ((elevation + 1) * density) + 1
        ));
    }

    private List<Fragment> getFragments(ArrayList<SongObject> songObjectList,
                                        ArrayList<ArtistObject> artistObjectList,
                                        ArrayList<AlbumObject> albumObjectList) {
        // An empty array list
        List<Fragment> fList = new ArrayList<Fragment>();
        // Initialize and add fragment objects to the array list
        fList.add(FragmentTracks.newInstance(songObjectList));
        fList.add(FragmentTracks.newInstance(songObjectList));
        fList.add(FragmentTracks.newInstance(songObjectList));
        fList.add(FragmentTracks.newInstance(songObjectList));  // 3 - TRACKS
        fList.add(FragmentArtists.newInstance(artistObjectList)); // 4 - ARTISTS
        fList.add(FragmentAlbums.newInstance(albumObjectList));
        fList.add(FragmentTracks.newInstance(songObjectList));
        // Return the fragment object array list for adaption to the pager view
        return fList;
    }

    public void mainFooterListener(final ArrayList<SongObject> songObjectList){
        LinearLayout mainFooter = (LinearLayout) findViewById(R.id.activity_main_footer);
        mainFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** dumb album columns **/
                MediaStoreInterface mint = new MediaStoreInterface(ctx);
                mint.dumpAlbumColumns();


                /*
                SpotifySolo spotify = new SpotifySolo(MainActivity.this,albumObjectList);
                ItunesSolo itunes = new ItunesSolo(MainActivity.this,albumObjectList);
                AmazonSolo amazon = new AmazonSolo(MainActivity.this,albumObjectList);


                for(int i=0;i<albumObjectList.size();i++){
                    if(albumObjectList.get(i).albumArtURI.equals("null")){

                        SignedRequestsHelper helper = new SignedRequestsHelper();
                        Map<String, String> values = new HashMap<String, String>();
                        values.put("Operation", "ItemSearch");
                        values.put("AssociateTag", "mytag-20");
                        values.put("SearchIndex", "Music");
                        values.put("Artist", albumObjectList.get(i).albumArtist);
                        values.put("Title", albumObjectList.get(i).albumTitle);
                        final String amazonurl = helper.sign(values);

                        Log.v("TAG","---------------------------------------------------");
                        Log.v("TAG","album is: "+albumObjectList.get(i).albumTitle);
                        Log.v("TAG","album art uri: "+albumObjectList.get(i).songObjectList.get(0).albumArtURI);
                        Log.v("TAG","spotify request url : "+spotify.spotifyUrl(albumObjectList.get(i).albumArtist,albumObjectList.get(i).albumTitle));
                        Log.v("TAG","itunes request url : "+itunes.getItunesRequestUrl(albumObjectList.get(i).albumArtist));
                        Log.v("TAG","amazon request url : " + amazonurl);
                        Log.v("TAG","---------------------------------------------------");
                    }
                }*/

                // Open the play panel
                Intent intent = new Intent(ActivityMain.this, ActivityPlayPanel.class);
                //intent.putExtra("songObjectList", songObjectList); //Optional parameters
                ActivityMain.this.startActivity(intent);
                //overridePendingTransition(R.anim.slide_up, R.anim.dont_move);

                // So here, if a a song is already playing, then we want to open
                // the new activity but without restarting the song.
                // If a song is not playing, then we want to play the first song on the list.
                if(StaticMusicPlayer.mediaPlayer.isPlaying()){
                    // do nothing
                } else {
                    StaticMusicPlayer.tryToPlaySong(songObjectList.get(0));
                }
            }
        });
    }

    private void scanMedia(){
        File file = new File(Environment.getExternalStorageDirectory() + "/Music");
        new SingleMediaScanner(this, file);
    }

    private String GetAlbumArtURI(String[] albumID) {
        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                albumID,
                null
        );
        if (mCursor.moveToFirst()) {

            //DatabaseUtils.dumpCursor(mCursor);
            String str = mCursor.getString(0);
            mCursor.close();
            return str;
        }
        else {
            mCursor.close();
            return null;
        }
    }


    private Cursor GetSongListCursor() {
        Uri contentURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String order = MediaStore.Audio.Media.TITLE + " ASC";
        final Cursor mCursor = getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        //DatabaseUtils.dumpCursor(mCursor);
        return mCursor;
    }

    // This method constructs our song objects and adds them to song object list and shuffled song object list
    // This method also constructs the album object and artist object and respective lists
    private void MakeLists(Cursor mCursor,
                           final ArrayList<SongObject> songObjectList,
                           final ArrayList<SongObject> songObjectList_shuffled,
                           final ArrayList<AlbumObject> albumObjectList,
                           final ArrayList<ArtistObject> artistObjectList) {
        MediaStoreInterface updatePath;
        try{
            if (mCursor.moveToFirst()) {
                do {
                    final SongObject songObject = new SongObject();

                    /** Making the song objects and adding them to the lists **/

                    // define artist property
                    String artist = mCursor.getString(1);
                    if (artist.equals("<unknown>")) {artist = "Unknown Artist"; songObject.artist = artist;}
                    else {artist = mCursor.getString(1);songObject.artist = artist;}
                    final String artistFinal = artist;

                    // define title, duration, path, albumId, title properties
                    final String albumTitle = mCursor.getString(0);
                    songObject.albumTitle = albumTitle;



                    final String songTitle = mCursor.getString(2);
                    songObject.songTitle = songTitle;
                    final String songPath = mCursor.getString(3);
                    songObject.songPath = songPath;
                    final String songDuration = mCursor.getString(4);
                    songObject.songDuration = songDuration;

                    // define albumId property
                    String[] albumID = {mCursor.getString(5)};
                    songObject.albumID = albumID[0];

                    // define albumArtUri property
                    songObject.albumArtURI = GetAlbumArtURI(albumID);


                    /** Download images **/
                    //if(songObject.albumArtURI.equals(targetValue)){ //If the data is an empty string
                    //updatePath = new MediaStoreInterface(ctx);
                    //updatePath.updateMediaStoreAudioAlbumsDataByAlbumId(Long.parseLong(albumID[0]), "Y"); //So we only do one album at a time
                    // requestList.add(songObject);
                    //}

                    // add song object to lists
                    songObjectList.add(songObject);
                    songObjectList_shuffled.add(songObject);

                    final String albumArtist = songObject.artist; // can an album have more than one artist? yes...
                    final String albumArtURI = songObject.albumArtURI;

                    /** Making the album object and adding it to the list **/
                    if(albumObjectList.size() != 0){
                        boolean songAdded = false;
                        for(int i=0;i<albumObjectList.size();i++){
                            if(albumObjectList.get(i).albumId == Integer.parseInt(songObject.albumID)){
                                albumObjectList.get(i).songObjectList.add(songObject);
                                songAdded = true;
                                //Log.v("TAG", "song added");
                                break;
                            }
                        }

                        if(songAdded == false){
                            //Log.v("TAG", "new album created");
                            albumObjectList.add(new AlbumObject(songObject));
                        }

                    } else {
                        albumObjectList.add(new AlbumObject(songObject));
                    }

                    /**
                     if(albumObjectList.isEmpty()){
                     albumObjectList.add(new AlbumObject(albumTitle,albumArtist,songObject.albumArtURI,songObject));
                     } else {

                     boolean bool = false;
                     for(int i = 0; i < albumObjectList.size(); i++) { // of the song is already added, don't add it again

                     if(albumObjectList.get(i).albumTitle.equals(albumTitle)) {
                     albumObjectList.get(i).songObjectList.add(songObject);
                     albumObjectList.get(i).albumArtURI = songObject.albumArtURI;
                     albumObjectList.get(i).albumId = Integer.parseInt(songObject.albumID);
                     Log.v("TAG","album id is here : "+songObject.albumID);
                     albumObjectList.get(i).albumTitle = songObject.albumTitle;
                     albumObjectList.get(i).albumArtist = songObject.artist;
                     albumObjectList.get(i).albumTrackCount += 1;

                     bool = true;
                     }
                     }

                     if(bool == false){ // if the song is not added, add it
                     AlbumObject newAlbumObject = new AlbumObject(albumTitle,albumArtist,albumArtURI,songObject);
                     newAlbumObject.albumTrackCount +=1;
                     albumObjectList.add(newAlbumObject);
                     }
                     }**/

                    /** Making the artist object and adding it to the list **/

                    if(artistObjectList.isEmpty()){
                        artistObjectList.add(new ArtistObject(songObject));
                    } else {

                        boolean bool = false;
                        for(int i = 0; i < artistObjectList.size(); i++) {

                            if(artistObjectList.get(i).artistName.equals(artistFinal)) {
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

    public class SongInfo {
        public String wrapperType;
        public String kind;
        public Integer artistId;
        public Integer collectionId;
        public Integer trackId;
        public String artistName;
        public String collectionName;
        public String trackName;
        public String collectionCensoredName;
        public String trackCensoredName;
        public String artistViewUrl;
        public String collectionViewUrl;
        public String trackViewUrl;
        public String previewUrl;
        public String artworkUrl30;
        public String artworkUrl60;
        public String artworkUrl100;
        public Float collectionPrice;
        public Float trackPrice;
        public String releaseDate;
        public String collectionExplicitness;
        public String trackExplicitness;
        public Integer discCount;
        public Integer discNumber;
        public Integer trackCount;
        public Integer trackNumber;
        public Integer trackTimeMillis;
        public String country;
        public String currency;
        public String primaryGenreName;
        public String radioStationUrl;
        public Boolean isStreamable;
    }


}
