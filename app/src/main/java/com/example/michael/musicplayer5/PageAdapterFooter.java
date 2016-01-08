package com.example.michael.musicplayer5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//PagerAdapter
//FragmentStatePagerAdapter

public class PageAdapterFooter extends PagerAdapter {

    private Context mContext;
    private ArrayList<SongObject> songObjectsList = StaticMusicPlayer.songObjectList;
    private SongObject songObject;

    static private int currentItem;

    public void setCurrentItem(int ci){
        currentItem = ci;
    }


    public PageAdapterFooter(Context context) {

        //http://stackoverflow.com/questions/17158817/extending-fragmentpageradapter-has-issues-with-a-missing-super-constructor
        //super(fm);
        this.mContext = context;
        //this.songObjectsList = songObjectsList;


    }


    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {

        songObject = songObjectsList.get(currentItem);
        //songObject = songObjectsList.get(ActivityMain.footerPager.getCurrentItem());


        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.page_adapter_footer, collection, false); //a view group is a layout, a view is a child of a view group

        //View page = inflater.inflate(R.layout.YOUR_PAGE, null);

        layout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click
                Log.v("TAG", "getCurrentItem is: "+ String.valueOf(ActivityMain.footerPager.getCurrentItem()));

                Intent intent = new Intent(mContext, ActivityPlayPanel.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("songObjectList", songObjectList); //Optional parameters
                mContext.startActivity(intent);


                if(StaticMusicPlayer.mediaPlayer.isPlaying() && StaticMusicPlayer.currentIndex == position){
                    // do nothing
                } else {
                    StaticMusicPlayer.tryToPlaySong(songObject);
                }

            }
        });


        //ViewGroup layout2 = (ViewGroup) inflater.inflate(R.layout.footer_album_layout, collection, false); //the second layout I want to adapt to

        collection.addView(layout);

        TextView footer_song_title_pager = (TextView) layout.findViewById(R.id.footer_song_title_pager);
        TextView footer_song_artist_pager = (TextView) layout.findViewById(R.id.footer_song_artist_pager);

        footer_song_title_pager.setText(songObjectsList.get(position).songTitle);
        footer_song_artist_pager.setText(songObjectsList.get(position).artist);


        return layout;

    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return songObjectsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /*
    @Override
    public CharSequence getPageTitle(int position) {
        CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }*/

}
