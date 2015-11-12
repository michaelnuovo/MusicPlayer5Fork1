package com.example.michael.musicplayer5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by michael on 11/1/15.
 */
public class ResizeBitMap {

    Context context;
    int bitMapWidth, bitMapHeight;
    int screenWidth, screenHeight;
    int width, height;
    Bitmap scaledBitmap;

    public ResizeBitMap(Context context){

        this.context = context;
    }

    public void GetDimensions(Bitmap unscaledBitmap){

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        bitMapWidth = unscaledBitmap.getWidth();
        bitMapHeight = unscaledBitmap.getHeight();
        Log.v("TAG screenWidth: ", String.valueOf(screenWidth));
        Log.v("TAG screenHeight: ", String.valueOf(screenHeight));
        Log.v("TAG bitMapWidth: ", String.valueOf(bitMapWidth));
        Log.v("TAG bitMapHeight: ", String.valueOf(bitMapHeight));

    }

    public Bitmap resize(Bitmap unscaledBitmap){

        GetDimensions(unscaledBitmap);

        if (screenWidth - bitMapWidth >= screenHeight - bitMapHeight) {

            width = screenWidth;
            height = bitMapHeight * (1 + screenWidth/bitMapWidth);
            scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, width, height, true); // Scale by width

            Log.v("TAG scaledBitmap width ", String.valueOf(width));
            Log.v("TAG scaledBitmap heigh ", String.valueOf(height));



        }else{

            width = bitMapWidth * (1 + screenHeight/bitMapHeight);
            height = screenHeight;
            scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, width, height, true); // Scale by height

            Log.v("TAG scaledBitmap width ", String.valueOf(width));
            Log.v("TAG scaledBitmap heigh ", String.valueOf(height));

            //bitMapWidth:: 351
            //bitMapHeight:: 351

            //screenWidth:: 1080
            //screenHeight:: 1920

            //scaledBitmap width: 2106
            //scaledBitmap height: 1920

        }

        return scaledBitmap;
    }
}
