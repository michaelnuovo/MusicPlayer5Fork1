package com.example.michael.musicplayer5;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

class MyPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String title;

    public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {

        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return this.fragments.get(position);
    }

    @Override
    public int getCount() {

        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position == 0){
            title = "OTHER";
        } else if (position == 1){
            title = "EQUALIZER";
        } else if (position == 2){
            title = "FAVORITES";
        } else if (position == 3) {
            title = "TRACKS";
        } else if (position == 4) {
            title = "ARTISTS";
        } else if (position == 5) {
            title = "ALBUMS";
        } else if (position == 6) {
            title = "OTHER";
        }

        return title;
    }

}