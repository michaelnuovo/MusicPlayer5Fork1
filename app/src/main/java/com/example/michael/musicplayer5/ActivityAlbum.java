package com.example.michael.musicplayer5;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityAlbum extends AppCompatActivity {

    AlbumObject currentAlbum = FragmentAlbums.currentAlbum;
    ListView listView;
    AdapterTracks adapter;
    static ViewPager footerPager;
    static public ToggleButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_album);

        playButton  = (ToggleButton) findViewById(R.id.main_playButton);

        listView = (ListView) findViewById(R.id.artistAlbumList);
        adapter = new AdapterTracks( this, R.layout.item_list_view2, currentAlbum.songObjectList); //takes context
        listView.setAdapter(adapter);

        if(!StaticMusicPlayer.musicIsPlaying && !StaticMusicPlayer.isPaused){StaticMusicPlayer.setList(currentAlbum.songObjectList);}
        footerPager = (ViewPager) findViewById(R.id.footerPager);
        final PageAdapterFooter pageAdapterFooter = new PageAdapterFooter(this);
        footerPager.setAdapter(new PageAdapterFooter(this));
        footerPager.setCurrentItem(StaticMusicPlayer.currentIndex);
        footerPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                pageAdapterFooter.setCurrentItem(footerPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        StaticMusicPlayer.setPlayButton(playButton);
        StaticMusicPlayer.setPlayButtonListener();
        StaticMusicPlayer.setShuffleList(returnShuffledList());
        StaticMusicPlayer.setShuffleButton((Button) findViewById(R.id.shuffleButton));
        StaticMusicPlayer.setShuffleButtonListener();

        setListItemClickListener();
        setFooterPagerClickListener();
    }

    public ArrayList<SongObject> returnShuffledList(){

        // Create new List with same capacity as original (for efficiency).
        ArrayList<SongObject> copy = new ArrayList<SongObject>(currentAlbum.songObjectList.size());

        for (SongObject foo: currentAlbum.songObjectList) {
            copy.add(foo.copy(new SongObject()));
        }

        Collections.shuffle(copy);

        return copy;
    }

    private void setListItemClickListener(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // Open the play panel activity from this fragment

                StaticMusicPlayer.setList(currentAlbum.songObjectList);
                StaticMusicPlayer.tryToPlaySong(currentAlbum.songObjectList.get(arg2)); // arg2 is a sonObject

                Intent intent = new Intent(getApplicationContext(), ActivityPlayPanel.class);
                startActivity(intent);
            }
        });
    }

    private void setFooterPagerClickListener(){

        footerPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // never called
                Log.v("TAG", "pager footer clicked");

            }
        });
    }
}
