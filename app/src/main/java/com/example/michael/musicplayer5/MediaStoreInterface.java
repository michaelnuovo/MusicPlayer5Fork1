package com.example.michael.musicplayer5;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by michael on 12/8/15.
 */
public class MediaStoreInterface {

    Context ctx;
    Bitmap bm;
    String lastImagePath;

    public MediaStoreInterface(Context ctx){
        this.ctx = ctx;
    }

    public String getLastImagePath(){
        return lastImagePath;
    }

    public void createFolder(String folderName){
        String root = Environment.getExternalStorageDirectory().toString();
        File directoryPath = new File(root + folderName);
        directoryPath.mkdirs();

    }

    public void clearFolder(String folderName){
        String root = Environment.getExternalStorageDirectory().toString();
        File directoryPath = new File(root + folderName);
        String[] children = directoryPath.list();
        for (int i = 0; i < children.length; i++) {
            new File(directoryPath, children[i]).delete();
        }
    }

    public void saveBitMapToFolderWithRandomNumericalName(String folderName, Bitmap bm){
        String root = Environment.getExternalStorageDirectory().toString();
        File filePath = new File(root + folderName);
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fileName = "Image-" + n + ".jpg";
        String imagePath = filePath + "/" + fileName;
        lastImagePath = imagePath;
        File file = new File(imagePath, fileName);
        if (file.exists()) file.delete(); // If a file of the same name exists, delete it.
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out); // Converts bitmap into JPEG.
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAndroidWithImagePath(String imagePath, String albumId){
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        ContentValues values = new ContentValues();
        //values.put("album_id", albumId);
        values.put("_data", imagePath);
        Uri newuri = ctx.getContentResolver().insert(sArtworkUri, values);
    }

    public void resetToNull(String fileName){

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        ContentValues values = new ContentValues();
        //values.put("_data", imagePath);
        Uri newuri = ctx.getContentResolver().insert(sArtworkUri, values);

        //Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        //if(String.valueOf(sArtworkUri).toLowerCase().contains(fileName.toLowerCase())){

        //}
    }

    public void dumpUpdatedImagePaths(String albumId){
        final Cursor mCursor = ctx.getContentResolver().query(
                android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{android.provider.MediaStore.Audio.Albums.ALBUM_ART},
                android.provider.MediaStore.Audio.Albums._ID + "=?",
                new String[]{albumId},
                null
        );
        if (mCursor.moveToFirst()) {

            DatabaseUtils.dumpCursor(mCursor);
            mCursor.close();
        }
        else {
            mCursor.close();
        }
    }

    public void dumpAllTrackInformation(){
        // Set getContentResolver().query(contentURI, projection, selection, null, order) arguments

        Uri contentURI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                android.provider.MediaStore.Audio.Media.ALBUM,
                android.provider.MediaStore.Audio.Media.ARTIST,
                android.provider.MediaStore.Audio.Media.TITLE,
                android.provider.MediaStore.Audio.Media.DATA,
                android.provider.MediaStore.Audio.Media.DURATION,
                android.provider.MediaStore.Audio.Media.ALBUM_ID};
        String selection = android.provider.MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String order = android.provider.MediaStore.Audio.Media.TITLE + " ASC";

        // Uri maps to the table in the provider named table_name.
        // projection is an array of columns that should be included for each row retrieved.
        // selection specifies the criteria for selecting rows.
        // selectionArgs is null
        // sortOrder specifies the order in which rows appear in the returned Cursor.

        // Initialize a local cursor object with a query and return the cursor object
        final Cursor mCursor = ctx.getContentResolver().query(contentURI, projection, selection, null, order);// Run getContentResolver query
        Log.v("TAG", "Dumping track information");
        DatabaseUtils.dumpCursor(mCursor);
        mCursor.close();
    }

    public void updateMediaStoreAudioAlbumsDataByAlbumId(Long albumId, String imagePath){ // <<-----------------<<< Update method

        /** Deletion **/
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        ctx.getContentResolver().delete(ContentUris.withAppendedId(albumArtUri, albumId), null, null);

        /** Insertion **/
        ContentValues insertionValues = new ContentValues();
        insertionValues.put("_data", imagePath);
        insertionValues.put("album_id", albumId);
        ctx.getContentResolver().insert(albumArtUri, insertionValues);

        /** Print results **/
        //dumpCursorByAlbumId(albumId.toString());
    }

    public void setAllAlbumDataToX(){
        for(int i=0;i<300;i++){

            Long albumId = (long) i;

            /** Deletion **/
            Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
            ctx.getContentResolver().delete(ContentUris.withAppendedId(albumArtUri, albumId), null, null);

            /** Insertion **/
            ContentValues insertionValues = new ContentValues();
            insertionValues.put("_data", "X");
            insertionValues.put("album_id", albumId);
            ctx.getContentResolver().insert(albumArtUri, insertionValues);

        }
    }

    public void dumpCursorByAlbumId(String id){
        String[] stringArray = {id};
        final Cursor mCursor = ctx.getContentResolver().query(
                android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, // Uri
                new String[]{                                 // String[] projection (columns)
                        android.provider.MediaStore.Audio.Albums._ID,
                        android.provider.MediaStore.Audio.Albums.ALBUM_ART,
                        android.provider.MediaStore.Audio.Albums.ARTIST,
                        android.provider.MediaStore.Audio.Albums.ARTIST,
                        android.provider.MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                        android.provider.MediaStore.Audio.Albums.ALBUM_KEY
                },
                android.provider.MediaStore.Audio.Albums._ID + "=?",           // String selection
                stringArray,                                  // String[] selectionArgs
                null                                         // sortOrder
        );

        if (mCursor.moveToFirst()) {

            // dump each row in the cursor
            // for(int i=0; i <  stringArray.length; i++){
            DatabaseUtils.dumpCursor(mCursor);
            // mCursor.moveToNext();
            //}

            mCursor.close();

        }
        else {
            mCursor.close();
        }
    }

    public void dumpAlbumColumns(){
        final Cursor mCursor = ctx.getContentResolver().query(
                android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, // Uri
                new String[]{                                 // String[] projection (columns)
                        android.provider.MediaStore.Audio.Albums._ID,
                        android.provider.MediaStore.Audio.Albums.ALBUM_ART,
                        android.provider.MediaStore.Audio.Albums.ARTIST,
                        android.provider.MediaStore.Audio.Albums.ARTIST,
                        android.provider.MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                        android.provider.MediaStore.Audio.Albums.ALBUM_KEY
                },
                null,           // String selection
                null,                                  // String[] selectionArgs
                null                                         // sortOrder
        );

        if (mCursor.moveToFirst()) {

            // dump each row in the cursor
            // for(int i=0; i <  stringArray.length; i++){
            DatabaseUtils.dumpCursor(mCursor);
            // mCursor.moveToNext();
            //}

            mCursor.close();

        }
        else {
            mCursor.close();
        }
    }
}
