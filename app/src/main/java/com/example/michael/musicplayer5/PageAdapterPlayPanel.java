package com.example.michael.musicplayer5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//PagerAdapter
//FragmentStatePagerAdapter

public class PageAdapterPlayPanel extends PagerAdapter {

    private Context mContext;
    private ArrayList<SongObject> songObjectsList = StaticMusicPlayer.songObjectList;
    private SongObject songObject;

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
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.pager_play_panel, collection, false); //a view group is a layout, a view is a child of a view group

        //ViewGroup layout2 = (ViewGroup) inflater.inflate(R.layout.footer_album_layout, collection, false); //the second layout I want to adapt to

        collection.addView(layout);

        TextView tx = (TextView) layout.findViewById(R.id.pagertest);
        tx.setText(String.valueOf(position));

        ImageView iv = (ImageView) layout.findViewById(R.id.slidingView);
        //Drawable drbl = Drawable.createFromPath(songObject.albumArtURI);

        /** Applies background to ViewPager **/
        if(songObject.albumArtURI != null){
            Bitmap bm = BitmapFactory.decodeFile(songObject.albumArtURI);
            //bm = Bitmap.createScaledBitmap(bm, 2, 2, false);
            iv.setImageBitmap(bm);
        }


        //int sdk = android.os.Build.VERSION.SDK_INT;
        //if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        //    iv.setBackgroundDrawable(drbl);
        //} else {
        //    iv.setBackground(drbl);
        //}

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
