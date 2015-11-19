package com.example.michael.musicplayer5;

import android.graphics.BitmapFactory;
import android.media.Image;

import java.io.File;

/**
 * Created by michael on 11/17/15.
 */
public final class IsImage {

    private IsImage(){

        // A private constructor that does nothing
    }

    static boolean test(File file){

        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }
}
