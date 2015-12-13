package com.example.michael.musicplayer5;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * This class will save a bitmap to disk as a jpg.
 * This class will also return the file path of the image with a getter method.
 */
public class SaveBitMapToDisk {

    String imagePath;

    public String getImagePath(){
        return imagePath;
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

    public void SaveImage(Bitmap finalBitmap, String folderName) {

        // Here we make the directory
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + folderName);
        myDir.mkdirs();

        //Delete all files in directory if there were any (for development purposes)
        //String[] children = myDir.list();
        //for (int i = 0; i < children.length; i++) {
        //    new File(myDir, children[i]).delete();
        //}

        // This is a random number generator for generating unique file names.
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fileName = "Image-" + n + ".jpg";

        imagePath = myDir + "/" + fileName;

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
