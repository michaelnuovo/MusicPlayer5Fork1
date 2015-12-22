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

    ArrayList<AlbumObject> requestList;
    Activity activity;

    public AlbumArt(Context ctx, ArrayList<AlbumObject> requestList){

        this.ctx = ctx;
        this.requestList=requestList;
        this.activity = (Activity) ctx;
        initFields();
    }

    public void resetPaths(){
        MediaStoreInterface mediaStore = new MediaStoreInterface(ctx);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for(int i=0;i<paths.size();i++){
            mmr.setDataSource(paths.get(i)[0]);
            byte [] data = mmr.getEmbeddedPicture();
            Bitmap bitmap;

            if(data!=null){ // not all albums have embedded art
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                // Save the bitmap to disk, return an image path
                SaveBitMapToDisk saveImage = new SaveBitMapToDisk();
                saveImage.SaveImage(bitmap, "myalbumart");
                String imagePathData = saveImage.getImagePath();

                // Update the image path to Android meta data
                Log.v("TAG","paths.get(i)[1] : "+paths.get(i)[1]);
                Log.v("TAG","imagePathData : "+imagePathData);
                mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(Long.parseLong(paths.get(i)[1]), imagePathData); // id / path
            } else {
                mediaStore.updateMediaStoreAudioAlbumsDataByAlbumId(Long.parseLong(paths.get(i)[1]), "null"); // id / path
                paths.get(i)[0]="null";
            }

        }




        //update songobject uri paths
        for(int i=0;i<requestList.size();i++){
            boolean match = false;
            while(match == false){
                for(int j=0;j<paths.size();j++){
                    Log.v("TAG","value of requestList.get(i).albumId is "+requestList.get(i).albumId);
                    Log.v("TAG","value of paths.get(j)[0]) is "+paths.get(j)[0]);
                    if(requestList.get(i).albumId == Integer.parseInt(paths.get(j)[0])){
                        for(int k=0;k<requestList.get(i).songObjectList.size();k++){
                            requestList.get(i).songObjectList.get(k).albumArtURI = paths.get(j)[1];
                        }
                        match = true;
                    }
                }
            }
        }



        //Log.v("TAG","value of activity is "+activity);
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

        Log.v("TAG","paths is : "+paths);

        //update songobject uri paths
        for(int i=0;i<requestList.size();i++){
            boolean match = false;
            while(match == false){
                for(int j=0;j<paths.size();j++){
                    Log.v("TAG","value of requestList.get(i).albumId is "+requestList.get(i).albumId);
                    Log.v("TAG","value of paths.get(j)[0]) is "+paths.get(j)[0]);
                    if(requestList.get(i).albumId == Integer.parseInt(paths.get(j)[1])){

                        for(int k=0;k<requestList.get(i).songObjectList.size();k++){
                            requestList.get(i).songObjectList.get(k).albumArtURI = paths.get(j)[1];
                        }
                        match = true;
                    }
                }
            }
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
                MediaStore.Audio.Media.ALBUM_ID};
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
            Log.v("TAG", "string is " + str);

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
