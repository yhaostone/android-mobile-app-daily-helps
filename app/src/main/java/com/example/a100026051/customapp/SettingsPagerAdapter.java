package com.example.a100026051.customapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by 100026051 on 10/30/16.
 */
public class SettingsPagerAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public SettingsPagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                SettingsTab1Fragment tab1 = new SettingsTab1Fragment();
                return tab1;

            case 1:
                SettingsTab2Fragment tab2 = new SettingsTab2Fragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
