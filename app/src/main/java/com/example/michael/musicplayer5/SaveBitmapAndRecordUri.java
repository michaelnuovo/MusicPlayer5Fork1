package com.example.michael.musicplayer5;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    public SaveBitmapAndRecordUri(Bitmap bm, String url, Context ctx, String albumID) {

        this.bm = bm;
        this.url = url;
        this.ctx = ctx;
        this.albumID = albumID;
    }

    public void run() {

        // So let's test the code and see if we can save the bitmaps to a file on the SD card
        SaveImage(bm);
        RecordURI();

    }

    public String getDir() {

        return dir;
    }

    private void SaveImage(Bitmap finalBitmap) {

        // Here we make the directory
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images_musicplayer5");
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

    private void RecordURI() {

        /* wrong way */

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Albums.ALBUM_ART, path);

        // update(Uri uri, ContentValues values, String where, String[] selectionArgs)
        int n = contentResolver.update(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                values,
                MediaStore.Audio.Albums.ALBUM_ID + "=" + albumID,
                null);


        Log.e(TAG, "updateAlbumImage(" + path + ", " + albumID + "): " + n);


        /* right way */

        ContentResolver res = ctx.getContentResolver();
        // where uri points to the table (not the row) content values point to a ContentValues object ... http://stackoverflow.com/questions/12525395/contenturis-withappendedid-method-in-android-content-provider-can-i-leave-the-i
        // so apparently sArtworkUri is the MediaStore.Audio.Albums.ALBUM_ART
        // Since I changed only one record, a URI with an appended ID is sufficient.
        // But if you want to update multiple values, you should use the normal URI and a selection clause."
        Uri uri = ContentUris.withAppendedId(Uri.parse(MediaStore.Audio.Albums.ALBUM_ART), Integer.parseInt(albumID));
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri); // Open a stream on to the content associated with a content URI.
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                // Bitmap bm = getArtworkFromFile(ctx, null, albumID);
                if (bm != null) {
                    // Put the newly found artwork in the database.
                    // Note that this shouldn't be done for the "unknown" album,
                    // but if this method is called correctly, that won't happen.

                    // first write it somewhere
                    // String file = Environment.getExternalStorageDirectory() + "/albumthumbs/" + String.valueOf(System.currentTimeMillis());

                    if (ensureFileExists(file)) {
                        try {
                            OutputStream outstream = new FileOutputStream(file);
                            if (bm.getConfig() == null) {
                                bm = bm.copy(Bitmap.Config.RGB_565, false);
                                if (bm == null) {
                                    return getDefaultArtwork(ctx);
                                }
                            }
                            boolean success = bm.compress(Bitmap.CompressFormat.JPEG, 75, outstream);
                            outstream.close();
                            if (success) {
                                ContentValues values = new ContentValues();
                                values.put("album_id", albumID);
                                values.put("_data", file);
                                Uri newuri = res.insert(sArtworkUri, values);
                                if (newuri == null) {
                                    // Failed to insert in to the database. The most likely
                                    // cause of this is that the item already existed in the
                                    // database, and the most likely cause of that is that
                                    // the album was scanned before, but the user deleted the
                                    // album art from the sd card.
                                    // We can ignore that case here, since the media provider
                                    // will regenerate the album art for those entries when
                                    // it detects this.
                                    success = false;
                                }
                            }
                            if (!success) {
                                File f = new File(file);
                                f.delete();
                            }
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, "error creating file", e);
                        } catch (IOException e) {
                            Log.e(TAG, "error creating file", e);
                        }
                    }
                } else {
                    bm = getDefaultArtwork(ctx);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

    }
}
