package com.snig.waterpurifier;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class IntroAdapter extends FragmentStatePagerAdapter {

    public IntroAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new Intro1Fragment();
            case 1: return new Intro2Fragment();
            case 2: return new Intro3Fragment();
            case 3: return new Intro4Fragment();
            default: return new Intro1Fragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
