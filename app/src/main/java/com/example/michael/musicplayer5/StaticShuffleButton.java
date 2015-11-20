package com.example.michael.musicplayer5;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by michael on 11/20/15.
 */
public final class StaticShuffleButton {

    private static Button shuffleButton = null;
    private static ArrayList<SongObject> shuffledList = null;

    private StaticShuffleButton(){

        // Private constructor that does nothing.
    }

    public static void setButton(Button btn){

        shuffleButton = btn;
    }

    public static void setButtonListener(){

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // So here, when the shuffle button is pressed from the main activity,
                // we want the play panel activity to open, and we want to
                // play the first song of a shuffled played list.
                // First we need to take the songObjectList, and we need to shuffle it
                // (which we do from the main activity).
                // Second we need to pass the shuffled list to static music player
                // (which we do here).
                // Third we need to call the static music player play method on the first song
                // in the shuffled list (which we do here).

                Log.v("TAG @#$@#FD@# ", String.valueOf(shuffledList.get(0).songTitle));

                StaticMusicPlayer.setSongObjectList(shuffledList);
                StaticMusicPlayer.tryToPlaySong(shuffledList.get(0));

                // We want to reshuffle the list again every time the user clicks on the shuffle button
                // since we want to save that list for the next time the user click on the button again.

                Collections.shuffle(shuffledList);
            }
        });
    }

    public static void setShuffledList(ArrayList<SongObject> arrayList){

        for(int i = 0; i < arrayList.size(); i++){

            Log.v("TAG sobject title A :", String.valueOf(arrayList.get(i).songTitle));
        }

        //Collections.shuffle(arrayList);
        //shuffledList = arrayList;

        ArrayList<SongObject> shuffledList = new ArrayList<>();
        for(SongObject item : arrayList) shuffledList.add(item);


        for(int i = 0; i < shuffledList.size(); i++){

            Log.v("TAG songobject title B", String.valueOf(arrayList.get(i).songTitle));

        }
    }

}
