package com.example.michael.musicplayer5;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.UserDictionary;
import android.renderscript.Sampler;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.Random;

/**
 * Created by michael on 12/2/15.
 */
public class SaveBitmapAndRecordUri {

    Bitmap bm;
    String url;
    //"https://itunes.apple.com/search?term=michael+jackson"
    String dir;
    Context ctx;
    String albumID;
    String fullPath;

    public SaveBitmapAndRecordUri(Bitmap bm, String url, Context ctx, String albumID) {

        this.bm = bm;
        this.url = url;
        this.ctx = ctx;
        this.albumID = albumID;
    }

    public void run() {

        // So let's test the code and see if we can save the bitmaps to a file on the SD card
        SaveImage(bm);


    }

    public String getDir() {

        return dir;
    }

    private void SaveImage(Bitmap finalBitmap) {

        // Here we make the directory
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/musicplayer5");
        myDir.mkdirs();

        // Delete all previous files in directory if there were any (for development purposes)
        String[] children = myDir.list();
        for (int i = 0; i < children.length; i++) {
            new File(myDir, children[i]).delete();
        }

        // This is a random number generator for generating unique file names.
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fileName = "Image-" + n + ".jpg";

        fullPath = myDir + "/" + fileName;

        dir = fileName;

        File file = new File(myDir, fileName);
        if (file.exists()) file.delete(); // here we overwrite the image file if it already exists (i.e. if the generated name is not unique, overwrite)
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); // Converts the bitmap into a JPEG (a compressed variant of the bitmap)
            out.flush(); // flushes anything which is still buffered by the OutputStream. See why JAVA uses a "buffer" for output streams https://docs.oracle.com/javase/tutorial/essential/io/buffers.html
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
