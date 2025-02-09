package com.gaiabit.gaiabit.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gaiabit.gaiabit.Fragments.Shelter;
import com.gaiabit.gaiabit.Fragments.ProfileFragment;
import com.gaiabit.gaiabit.Fragments.Home;
import com.gaiabit.gaiabit.Fragments.add;
import com.gaiabit.gaiabit.Fragments.Search;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int noOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            default:

                case 0:
                    return new Home();
                case 1:
                    return new add();
                case 2:
                    return new ProfileFragment();
                case 3:
                    return new Shelter();
                case 4:
                    return new Search();

        }
    }


    @Override
    public int getCount() {
        return noOfTabs;
    }
}
