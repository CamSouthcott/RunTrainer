package com.camsouthcott.runtrainer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Cam Southcott on 12/26/2015.
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter{

    private String tabTitles[] = new String[]{"Main","History"};

    public MyFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch(i){
            case 0:
                return new MainFragment();
            case 1:
                return new RecordsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
