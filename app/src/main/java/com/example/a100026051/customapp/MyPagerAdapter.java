package com.example.a100026051.customapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by 100026051 on 10/30/16.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public MyPagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Tab1Fragment tab1 = new Tab1Fragment();
                return tab1;

            case 1:
                Tab2Fragment tab2 = new Tab2Fragment();
                return tab2;

            case 2:
                Tab3Fragment tab3 = new Tab3Fragment();
                return tab3;

            case 3:
                Tab4Fragment tab4 = new Tab4Fragment();

                return tab4;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
