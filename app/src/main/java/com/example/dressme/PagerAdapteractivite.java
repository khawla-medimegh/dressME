package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapteractivite extends FragmentPagerAdapter {
    private int numOfTabs;

    public PagerAdapteractivite(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       switch (position){
           case 0:
               return new fragment_request();
           case 1:
               return new fragment_progressactivite();
           default:
               return null;
       }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
