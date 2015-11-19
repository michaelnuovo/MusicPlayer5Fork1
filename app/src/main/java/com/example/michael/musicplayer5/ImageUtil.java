package com.example.michael.musicplayer5;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by michael on 11/18/15.
 */
public final class ImageUtil {

    private ImageUtil(){

        // Private constructors that does nothing
    }

    static public String saveToInternalSorage(Context ctx, String imageDir, String imageName, Bitmap bitmapImage){

        ContextWrapper cw = new ContextWrapper(ctx);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(imageDir, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,imageName);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    static public Bitmap loadImageFromStorage(String imageDir, String imageName) {

        try {
            String path = "/data/data/yourapp/app_data/" + imageDir;
            File f = new File(path, imageName);
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f));

            return bm;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
