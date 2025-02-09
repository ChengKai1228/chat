package com.gaiabit.gaiabit;


import static com.gaiabit.gaiabit.utils.Constants.PREF_DIRECTORY;
import static com.gaiabit.gaiabit.utils.Constants.PREF_NAME;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;


import androidx.appcompat.widget.Toolbar;

import com.gaiabit.gaiabit.Fragments.Search;
import com.gaiabit.gaiabit.adapter.ViewPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements Search.OnDataPass{


    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;
    ViewPagerAdapter pagerAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        addTabs();

    }

    private void init() {

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);

    }

    private void addTabs() {

//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_heart));

        List<Integer> drawableResList = new ArrayList<>();
        drawableResList.add(R.drawable.ic_home);
        drawableResList.add(R.drawable.baseline_add_24);
        drawableResList.add(R.drawable.baseline_person_24);
        drawableResList.add(R.drawable.baseline_location_on_24);
        drawableResList.add(R.drawable.ic_search);

        for (int i = 0; i < 5; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(drawableResList.get(i)));
        }


        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String directory = preferences.getString(PREF_DIRECTORY, "");





        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {

                    case 0:
//                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);
                        tab.setIcon(R.drawable.ic_home);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.baseline_add_24);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.baseline_person_24);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.baseline_location_on_24);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.ic_search);
                        break;


                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
//                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);
                        tab.setIcon(R.drawable.ic_home);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.baseline_add_24);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.baseline_person_24);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.baseline_location_on_24);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.ic_search);
                        break;


                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
//                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);
                        tab.setIcon(R.drawable.ic_home);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.baseline_add_24);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.baseline_person_24);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.baseline_location_on_24);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.ic_search);
                        break;

                }

            }
        });

    }


    @Override
    public void onChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER = true;
        viewPager.setCurrentItem(5);
    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 5) {
            viewPager.setCurrentItem(0);
            IS_SEARCHED_USER = false;
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus(true);
    }

    @Override
    protected void onPause() {
        updateStatus(false);
        super.onPause();
    }

    void updateStatus(boolean status) {

        Map<String, Object> map = new HashMap<>();
        map.put("online", status);

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUid())
                .update(map);
    }

}

