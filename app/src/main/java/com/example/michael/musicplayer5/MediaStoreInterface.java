package com.example.michael.musicplayer5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

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
        values.put("album_id", albumId);
        values.put("_data", imagePath);
        Uri newuri = ctx.getContentResolver().insert(sArtworkUri, values);
    }

    public void dumpCursor(String albumId){
        final Cursor mCursor = ctx.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
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
}
