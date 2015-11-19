package com.example.michael.musicplayer5;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class SingleMediaScanner implements MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private File mFile;

    // This class registers File F with media store

    public SingleMediaScanner(Context context, File f) {
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        //mMs.scanFile(mFile.getAbsolutePath(), null);

        /*
        File file = new File(Environment.getExternalStorageDirectory() + "/Download");
        for(File f : file.listFiles()) {
            mMs.scanFile(f.getAbsolutePath(), null);
        }*/

        listDirectory(mFile,1);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
    }

    public void listDirectory(File dirPath, int level) {

        //File dir = new File(dirPath);

        File[] firstLevelFiles = dirPath.listFiles();
        if (firstLevelFiles != null && firstLevelFiles.length > 0) {
            for (File aFile : firstLevelFiles) {
                for (int i = 0; i < level; i++) {
                    //System.out.print("\t");

                }
                if (aFile.isDirectory()) {
                    //System.out.println("[" + aFile.getName() + "]");
                    listDirectory(aFile, level + 1);
                } else {
                    //Log.v("TAG file name",aFile.getName());
                    // May I should only scan the file if it is a music file
                    mMs.scanFile(aFile.getAbsolutePath(), null);
                }
            }
        }
    }
}