package com.example.android.yourpodcastapp.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.yourpodcastapp.Fragments.FavoritedFragment;
import com.example.android.yourpodcastapp.Fragments.SubscribedFragment;

/**
 * Created by Szymon on 07.02.2019.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    //Adapter used for pagerview
    private Context mContext;
//Planning to use images instead of String Tabs

    /**
     * Provides the appropriate {@link Fragment} for a view pager.
     */
    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SubscribedFragment();
        } else {
            return new FavoritedFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

//    Inspired by
//    https://stackoverflow.com/questions/31260384/how-to-add-page-title-and-icon-in-android-fragmentpageradapter


    @Override
    public CharSequence getPageTitle(int position) {

        return null;
    }
}