/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Profile.Profile;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Trips.Trips;

public class AnalyzeTabNavigation extends AppCompatActivity {
    private AnalyzeMyDriving analyzeMyDriving;
    private Profile profile;
    private Trips trips;

    private TabLayout tabBar;

    final private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    final private ArrayList<String> tabTitles = new ArrayList<String>(Arrays.asList("Drive", "Profile", "Trips"));

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_navigation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        tabBar = (TabLayout) findViewById(R.id.tabBar);

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        initalizeFragments();
        getTabTitle();
    }

    @Override
    public void onBackPressed() {
        ActivityHelper.jumpToFirstPage(this);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void initalizeFragments() {
        analyzeMyDriving = new AnalyzeMyDriving();
        fragments.add(analyzeMyDriving);

        AnalyzeMyDriving.behaviorDemo = true;
        AnalyzeMyDriving.needCredentials = true;

        profile = new Profile();
        fragments.add(profile);

        trips = new Trips();
        fragments.add(trips);
    }

    private void getItem(final int position) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.tabsFrame, fragments.get(position));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void getTabTitle() {
        for (int i = 0; i < tabTitles.size(); i++) {
            final String text = tabTitles.get(i);
            final TabLayout.Tab tab = tabBar.newTab();
            if (i == 0) {
                tabBar.addTab(tab.setText(text), true);
            } else {
                tabBar.addTab(tab.setText(text));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}