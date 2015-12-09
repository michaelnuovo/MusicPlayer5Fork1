package com.example.michael.musicplayer5;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //testing

    static ArrayList<SongObject> mainList = null; // we need this variables to be global so that it can be referenced from other activities
    private static Context mContext;
    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onResume(){

        /** Set play button and play button listener **/
        StaticMusicPlayer.setPlayButton((ToggleButton) findViewById(R.id.main_playButton));
        StaticMusicPlayer.setPlayButtonListener();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        /** Make song object list, album object list, and artist object list **/
        ArrayList<SongObject> songObjectList = new ArrayList<>();
        ArrayList<SongObject> songObjectList_shuffled = new ArrayList<>();
        ArrayList<AlbumObject> albumObjectList = new ArrayList<>();
        ArrayList<ArtistObject> artistObjectList = new ArrayList<>();
        mainList = songObjectList;
        StaticMusicPlayer.setList(mainList);

        /** Populate lists **/
        scanMedia();
        Cursor songListCursor = GetSongListCursor();
        MakeLists(songListCursor, songObjectList, songObjectList_shuffled, albumObjectList, artistObjectList);
        Collections.shuffle(songObjectList_shuffled);

        /** Initialize static music player **/
        StaticMusicPlayer.setList(songObjectList);

        /** Set adapter for main activity **/
        List<Fragment> fragments = getFragments(songObjectList, artistObjectList, albumObjectList);
        MyPageAdapterMain pageAdapter = new MyPageAdapterMain(getSupportFragmentManager(), fragments);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setPageTransformer(true, new DepthPageTransformer());
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

        /** Draw Footer Shadow
        float elevation = 2;
        float density = getResources().getDisplayMetrics().density;

        View v = findViewById(R.id.shadowView);
        v.setBackgroundDrawable(new RoundRectDrawableWithShadow(
                getResources(), Color.BLACK, 0,
                elevation * density, ((elevation + 1) * density) + 1
        ));**/

        /** MediaStoreInterface **/
        MediaStoreInterface msi = new MediaStoreInterface(this);
        msi.clearFolder("folderName");
        msi.createFolder("folderName");
        msi.saveBitMapToFolderWithRandomNumericalName("folderName", bitmap);
        String imagePath = msi.getLastImagePath();
        msi.updateAndroidWithImagePath(imagePath, albumId);
        msi.dumpCursor(albumId);
    }

    private List<Fragment> getFragments(ArrayList<SongObject> songObjectList,
                                        ArrayList<ArtistObject> artistObjectList,
                                        ArrayList<AlbumObject> albumObjectList) {


        // An empty array list
        List<Fragment> fList = new ArrayList<Fragment>();

        // Initialize and add fragment objects to the array list

        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));  // 3 - TRACKS
        fList.add(MyFragmentArtists.newInstance(artistObjectList)); // 4 - ARTISTS



        fList.add(MyFragmentAlbums.newInstance(albumObjectList));
        fList.add(MyFragmentTracks.newInstance(songObjectList));

        // Return the fragment object array list for adaption to the pager view
        return fList;
    }

    public void mainFooterListener(final ArrayList<SongObject> songObjectList){

        LinearLayout mainFooter = (LinearLayout) findViewById(R.id.activity_main_footer);

        mainFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open the play panel

                Intent intent = new Intent(MainActivity.this, PlayPanelActivity.class);
                //intent.putExtra("songObjectList", songObjectList); //Optional parameters
                MainActivity.this.startActivity(intent);
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

            String var = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            DatabaseUtils.dumpCursor(mCursor);
            mCursor.close();
            return var;
        }
        else {
            mCursor.close();
            return null;
        }
    }



    private Cursor GetSongListCursor() {

        // Set getContentResolver().query(contentURI, projection, selection, null, order) arguments

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

        // Uri maps to the table in the provider named table_name.
        // projection is an array of columns that should be included for each row retrieved.
        // selection specifies the criteria for selecting rows.
        // selectionArgs is null
        // sortOrder specifies the order in which rows appear in the returned Cursor.

        // Initialize a local cursor object with a query and return the cursor object
        final Cursor mCursor = getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        //DatabaseUtils.dumpCursor(mCursor);
        return mCursor;
    }



    private void MakeLists(Cursor mCursor,
                           ArrayList<SongObject> songObjectList,
                           ArrayList<SongObject> songObjectList_shuffled,
                           ArrayList<AlbumObject> albumObjectList,
                           ArrayList<ArtistObject> artistObjectList) {

        try{

            if (mCursor.moveToFirst()) {

                do {



                    // Initialize songObject
                    final SongObject songObject = new SongObject();

                    String songTitle = mCursor.getString(2);
                    songObject.songTitle = songTitle;

                    String songPath = mCursor.getString(3);
                    songObject.songPath = songPath;

                    String songDuration = mCursor.getString(4);
                    songObject.songDuration = songDuration;

                    // Add albumURI
                    String[] albumID = {mCursor.getString(5)};
                    songObject.albumArtURI = GetAlbumArtURI(albumID);

                    // Add albumId
                    songObject.albumID = albumID[0];

                    //Log.v("TAG","URI is "+String.valueOf(songObject.albumArtURI));

                    if(songObject.albumArtURI != null){

                        // The URI exists, so we leave as is

                    } else {

                        // do nothing
                        // null albumArtURI will be handled by Picasso and Volley in the adapter classes
                    }

                    /* else { // search folder for image file e.g. a jpg
                        String dirPath = songObject.songPath;
                        dirPath = dirPath.substring(0,dirPath.lastIndexOf('/'));
                        File mFile = new File(dirPath);

                        File[] mFiles = mFile.listFiles();

                        for(File aFile : mFiles){

                            if(IsImage.test(aFile) == true){

                                // Get the absolute path of the file if its an image
                                songObject.albumArtURI = aFile.getAbsolutePath();

                                break;

                            } else {
                                //get some other image

                                //songObject.albumArtURI = String.valueOf(Uri.parse("android.resource://"+PACKAGE_NAME+"/" + R.drawable.mdefault));

                                // data/data/yourapp/app_data/imageDir




                                //Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.mdefault);
                                //ImageUtil.saveToInternalSorage(this, "imageDir", "defaultImage", bm);
                                //String PACKAGE_NAME = getApplicationContext().getPackageName();
                                //songObject.albumArtURI = "data/data/"+PACKAGE_NAME+"/app_data/" + "imageDir";





                            }
                        }
                    }*/

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


                    songObjectList.add(songObject);
                    songObjectList_shuffled.add(songObject);

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
