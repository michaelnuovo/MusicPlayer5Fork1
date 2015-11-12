package com.example.michael.musicplayer5;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by michael on 11/1/15.
 */
public class ScreenDimensions {


    int height;
    int width;

    public ScreenDimensions(Context ctx){

        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        this.width = size.x;
        this.height = size.y;
    }
}
