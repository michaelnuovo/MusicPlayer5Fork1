package com.example.michael.musicplayer5;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

//PagerAdapter
//FragmentStatePagerAdapter

public class PageAdapterPlayPanel extends PagerAdapter {

    private Context mContext;
    private ArrayList<SongObject> songObjectsList = StaticMusicPlayer.songObjectList;
    private SongObject songObject;

    boolean clicked = false;
    boolean paused = false;

    public PageAdapterPlayPanel(Context context) {

        //http://stackoverflow.com/questions/17158817/extending-fragmentpageradapter-has-issues-with-a-missing-super-constructor
        //super(fm);
        this.mContext = context;
        //this.songObjectsList = songObjectsList;

    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        songObject = songObjectsList.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.pager_play_panel, collection, false);
        collection.addView(layout);



        View l1 = layout.findViewById(R.id.pager_art_layout);
        View l2 = layout.findViewById(R.id.pager_controls_layout);


        layout.setTag(position);

        l1.setTag(position + "l1");
        l2.setTag(position + "l2");

        developmentStuff();
        setButtons(layout);
        setBackground(l1, l2);
        isPausedListener(l1, l2);
        setAnimation(l1, l2, position, collection);

        return layout;
    }

    public void developmentStuff(){

        //TextView tx = (TextView) layout.findViewById(R.id.pagertest);
        //tx.setText(String.valueOf(position));
    }

    public void setButtons(ViewGroup layout){

        StaticMusicPlayer.skipRightButton((Button) layout.findViewById(R.id.skipforwards)); // set static reference to this object
        StaticMusicPlayer.setSkipForwardsListener();
        StaticMusicPlayer.skipLeftButton((Button) layout.findViewById(R.id.skipback));
        StaticMusicPlayer.setSkipBackwardsListener();
        StaticMusicPlayer.setPlayButton((ToggleButton) layout.findViewById(R.id.playbutton));
        StaticMusicPlayer.setPlayButtonListener();
    }

    public void setBackground(View l1,View l2){

        if(songObject.albumArtURI != null){
            Bitmap bm = BitmapFactory.decodeFile(songObject.albumArtURI);
            BitmapDrawable dw = new BitmapDrawable(bm);
            l1.setBackgroundDrawable(dw);

            String blurdark = songObject.albumArtURI.replace(".jpg","BlurredDark.jpg"); //all
            Bitmap bm2 = BitmapFactory.decodeFile(blurdark);
            BitmapDrawable dw2 = new BitmapDrawable(bm2);
            l2.setBackgroundDrawable(dw2);
        }
    }

    public void isPausedListener(View l1,View l2){

        if(StaticMusicPlayer.isPaused){
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
            l2.bringToFront();

            //ActivityPlayPanel.resetPager();

            //ActivityPlayPanel.playPager.findViewWithTag(ActivityPlayPanel.playPager.getCurrentItem()+1).setVisibility(l1.GONE);
            //ActivityPlayPanel.playPager.findViewWithTag(ActivityPlayPanel.playPager.getCurrentItem()+1).setVisibility(l2.VISIBLE);

            Log.v("TAG","ActivityPlayPanel.playPager.getCurrentItem is "+ActivityPlayPanel.playPager.getCurrentItem());



        } else {
            l2.setVisibility(View.GONE);
            l1.setVisibility(View.VISIBLE);
            l1.bringToFront();

        }
    }

    public void setAnimation(final View l1, final View l2,final int position, final ViewGroup collection){

        final Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        final Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG", "L1 clicked");

                if (clicked) {

                    //do nothing

                } else {

                    clicked = true;

                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Log.v("TAG", "L1 animation start");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Log.v("TAG", "L1 animation end");
                            //try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
                            l1.setVisibility(View.GONE);
                            l2.bringToFront();
                            Log.v("TAG", "L1 visibility set to gone");
                            //try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                            clicked = false;


                            View layout = collection.findViewWithTag(position+1);
                            layout.findViewById(R.id.pager_art_layout).setVisibility(View.GONE);
                            layout.findViewById(R.id.pager_controls_layout).setVisibility(View.VISIBLE);
                        }
                    });
                    Log.v("TAG", "L1 start animation");
                    l1.startAnimation(fadeOut);
                    l2.setVisibility(View.VISIBLE);
                    //l2.startAnimation(fadeIn);
                }
            }
        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("TAG", "L2 clicked");

                if (clicked) {

                    //do nothing

                } else {

                    clicked = true;

                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            //try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
                            l2.setVisibility(View.GONE);
                            l1.bringToFront();
                            Log.v("TAG", "L2 visibility set to gone");
                            clicked = false;
                            //startUpdate(layout);

                        }
                    });
                    l2.startAnimation(fadeOut);
                    l1.setVisibility(View.VISIBLE);
                    //l1.startAnimation(fadeIn);
                }
            }
        });
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
