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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Home.CarBrowse;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Profile.Profile;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Reservations.Reservations;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Trips.Trips;

public class tabNavigation extends AppCompatActivity {
    private CarBrowse carBrowse;
    private Reservations reservations;
    private Profile profile;
    private Trips trips;

    private TabLayout tabBar;

    private final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private final ArrayList<String> tabTitles = new ArrayList<String>(Arrays.asList("Search", "Reservations", "Profile", "Trips"));

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_navigation);

        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setElevation(0);

        tabBar = (TabLayout) findViewById(R.id.tabBar);

        final Intent i = getIntent();
        final String name = i.getStringExtra("next_activity");
        int position = 0;
        if (name != null) {
            if ("search".equals(name)) {
                position = 0;
            } else if ("reservations".equals(name)) {
                position = 1;
            } else if ("profile".equals(name)) {
                position = 2;
            } else if ("trips".equals(name)) {
                position = 3;
            } else {
                Log.e("Error", "unknown fragment name: " + name);
            }
        }
        // System.out.println("NEXT " + name + " " + position);

        initalizeFragments();
        getTabTitle(position);
        getItem(position);

        // adding this listener must be after getTabTitle() and getItem(); otherwise this listener calls getItems() duplicatedly
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
        carBrowse = new CarBrowse();
        fragments.add(carBrowse);

        reservations = new Reservations();
        fragments.add(reservations);

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

    private void getTabTitle(int positionSelected) {
        if (positionSelected < 0 || positionSelected >= tabTitles.size()) {
            positionSelected = 0;
        }
        for (int i = 0; i < tabTitles.size(); i++) {
            final String text = tabTitles.get(i);
            final TabLayout.Tab tab = tabBar.newTab();
            if (i == positionSelected) {
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
