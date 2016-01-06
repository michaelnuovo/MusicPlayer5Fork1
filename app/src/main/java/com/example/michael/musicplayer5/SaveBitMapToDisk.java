package com.example.michael.musicplayer5;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * This class will save a bitmap to disk as a jpg.
 * This class will also return the file path of the image with a getter method.
 */
public class SaveBitMapToDisk {

    String imagePath1;
    String imagePath2;
    Bitmap blurredAndDarkened;

    public String getImagePath1(){
        return imagePath1;
    }

    public void deleteByFileName(String folderName, String fileName){

        //Delete all files in directory if there were any (for development purposes)

        String root = Environment.getExternalStorageDirectory().toString(); // get external directory
        File myDir = new File(root + "/" + folderName); // get file path from external directory + folder name
        for(File f : myDir.listFiles()) {
            if(String.valueOf(f).equals(fileName)){
                f.delete();
            }
        }
    }

    public void SaveImage(final Bitmap finalBitmap, String folderName) {

        blurredAndDarkened = darkenBitMap(FastBlur.fastblur(finalBitmap, 1, 5));

        /**
        new Thread() {
            public void run() {
                blurredAndDarkened = darkenBitMap(FastBlur.fastblur(finalBitmap, 1, 5));
            }
        }.start();**/


        //Create directory
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + folderName);
        myDir.mkdirs();

        //Delete all files in directory if there were any (for development purposes)
        //String[] children = myDir.list();
        //for (int i = 0; i < children.length; i++) {
        //    new File(myDir, children[i]).delete();
        //}

        //Generate random number
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);

        //Name files
        String fileName1 = "Image-" + n + ".jpg";
        String fileName2 = "Image-" + n + "BlurredDark" + ".jpg";

        //Set image paths
        imagePath1 = myDir + "/" + fileName1;
        imagePath2 = myDir + "/" + fileName2;

        //Make files
        File file1 = new File(myDir, fileName1);
        File file2 = new File(myDir, fileName2);

        //Test existence of files
        if (file1.exists()) file1.delete();
        if (file2.exists()) file2.delete();

        //Write images to files
        try {

            FileOutputStream out1 = new FileOutputStream(file1);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out1); // Converts the bitmap into a JPEG (a compressed variant of the bitmap)

            out1.flush(); // flushes anything which is still buffered by the OutputStream. See why JAVA uses a "buffer" for output streams https://docs.oracle.com/javase/tutorial/essential/io/buffers.html
            out1.close();

            FileOutputStream out2 = new FileOutputStream(file2);
            blurredAndDarkened.compress(Bitmap.CompressFormat.JPEG, 90, out2); // Converts the bitmap into a JPEG (a compressed variant of the bitmap)

            out2.flush(); // flushes anything which is still buffered by the OutputStream. See why JAVA uses a "buffer" for output streams https://docs.oracle.com/javase/tutorial/essential/io/buffers.html
            out2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap darkenBitMap(Bitmap bm) {

        Canvas canvas = new Canvas(bm);
        Paint p = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(Color.RED, 1);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // to lighten
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000); // to darken
        p.setColorFilter(filter);
        canvas.drawBitmap(bm, new Matrix(), p);

        return bm;
    }
}
