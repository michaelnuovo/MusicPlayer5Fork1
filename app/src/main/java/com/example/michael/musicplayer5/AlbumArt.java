package com.example.michael.musicplayer5;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by michael on 12/20/15.
 */
public class AlbumArt {

    Context ctx;
    ArrayList<String[]> paths = new ArrayList();

    ArrayList<AlbumObject> albumObjectsList;
    Activity activity;

    public AlbumArt(Context ctx, ArrayList<AlbumObject> albumObjectsList){

        this.ctx = ctx;
        this.albumObjectsList=albumObjectsList;
        this.activity = (Activity) ctx;
        initFields();
    }

    public void resetPaths(){

        MediaStoreInterface mediaStore = new MediaStoreInterface(ctx);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Bitmap bitmap;

        for(int i=0; i<albumObjectsList.size();i++){
            String songPath = albumObjectsList.get(i).songObjectList.get(0).songPath; //get a song path from a song in the album
            mmr.setDataSource(songPath);
            byte [] data = mmr.getEmbeddedPicture();

            if(data!=null){ // if the songs in the album have embedded do this
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                // Save the bitmap to disk, return an image path
                SaveBitMapToDisk saveImage = new SaveBitMapToDisk();
                saveImage.SaveImage(bitmap, "myalbumart");
                String imagePathData = saveImage.getImagePath1();

                // Update the image path to Android meta data
                //Log.v("TAG","paths.get(i)[1] : "+paths.get(i)[1]);
                //Log.v("TAG","imagePathData : "+imagePathData);
                mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(Long.valueOf(albumObjectsList.get(i).albumId), imagePathData); // id / path

                //update the album uri path
                albumObjectsList.get(i).albumArtURI = imagePathData;

                //update the uri paths of the songs in the album object
                for(int j=0; j<albumObjectsList.get(i).songObjectList.size();j++){
                    albumObjectsList.get(i).songObjectList.get(j).albumArtURI = imagePathData;
                }

            } else { //if the songs in the album do not have embedded art, set their paths to null

                //update the album uri path
                albumObjectsList.get(i).albumArtURI = "null";

                //update the meta data
                mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(Long.valueOf(albumObjectsList.get(i).albumId), "null"); // id / path

                //update the songs in the album
                for(int j=0; j<albumObjectsList.get(i).songObjectList.size();j++){
                    albumObjectsList.get(i).songObjectList.get(j).albumArtURI = "null";
                }
            }
        }

        //update the adapters
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                UpdateAdapters.getInstance().update();

            }
        });
    }

    public void setAllPathsToNull(){

        MediaStoreInterface mediaStore = new MediaStoreInterface(ctx);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for(int i=0;i<paths.size();i++){

                mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(Long.parseLong(paths.get(i)[1]), "null"); // id / path
                paths.get(i)[0]="null";


        }

        //Log.v("TAG","paths is : "+paths);

        //update songobject uri paths
        for(int i=0;i<albumObjectsList.size();i++){
            boolean match = false;
            while(match == false){
                for(int j=0;j<paths.size();j++){
                    Log.v("TAG","value of requestList.get(i).albumId is "+albumObjectsList.get(i).albumId);
                    Log.v("TAG","value of paths.get(j)[0]) is "+paths.get(j)[0]);
                    if(albumObjectsList.get(i).albumId == Integer.parseInt(paths.get(j)[1])){ // path / id

                        for(int k=0;k<albumObjectsList.get(i).songObjectList.size();k++){
                            albumObjectsList.get(i).songObjectList.get(k).albumArtURI = paths.get(j)[1];
                        }
                        match = true;
                    }
                }
            }
        }

        for(int i=0;i<albumObjectsList.size();i++){
            //Log.v("TAG","albumObjectList.get(i).albumTitle is "+albumObjectList.get(i).albumTitle);
            //Log.v("TAG","albumObjectList.get(i).albumTitle uri is "+albumObjectList.get(i).albumArtURI);
            albumObjectsList.get(i).albumArtURI="null";
        }


        //Log.v("TAG","value of activity is "+activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                UpdateAdapters.getInstance().update();

            }
        });

    }

    public ArrayList initFields() {
        Uri contentURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String order = MediaStore.Audio.Media.TITLE + " ASC";
        final Cursor mCursor = ctx.getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        //DatabaseUtils.dumpCursor(mCursor);
        //return mCursor;

        //if (mCursor.moveToFirst())

        while (mCursor.moveToNext()) {

            //DatabaseUtils.dumpCursor(mCursor);
            String str = mCursor.getString(3);
            String id = mCursor.getString(5);
            String[] element = {str,id}; // path / id
            paths.add(element);
            //Log.v("TAG", "string is " + str);

            //return str;
        }

        mCursor.close();
        return paths;
        //else {
        //    mCursor.close();
            //return null;
        //}
    }

    public void dumpMusicColumns(){

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
        final Cursor mCursor = ctx.getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        DatabaseUtils.dumpCursor(mCursor);
        mCursor.close();

    }

    //MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

    public void dumpAlbumColumns(){
        Uri contentURI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums._ID};
        String selection = null;
        String order = null;
        final Cursor mCursor = ctx.getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        DatabaseUtils.dumpCursor(mCursor);
        mCursor.close();


    }
}
