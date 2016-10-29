package team5_project.cs442.eventorganizer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import team5_project.cs442.eventorganizer.R;
import team5_project.cs442.eventorganizer.TabFragments.FragmentTab1;
import team5_project.cs442.eventorganizer.TabFragments.FragmentTab2;
import team5_project.cs442.eventorganizer.TabFragments.FragmentTab3;
import team5_project.cs442.eventorganizer.TabFragments.FragmentTab4;
import team5_project.cs442.eventorganizer.TabFragments.FragmentTab5;
import team5_project.cs442.eventorganizer.database.Database;
import team5_project.cs442.eventorganizer.event.Event;
import team5_project.cs442.eventorganizer.event.Tuple;

/**
 * Created by Anna on 11/20/2015.
 */
public class TabActivity extends BaseActivity {
    ActionBar.Tab tab1, tab2, tab3, tab4, tab5;
    android.app.Fragment fragmentTab1 = new FragmentTab1();
    android.app.Fragment fragmentTab2 = new FragmentTab2();
    android.app.Fragment fragmentTab3 = new FragmentTab3();
    android.app.Fragment fragmentTab4 = new FragmentTab4();
    android.app.Fragment fragmentTab5 = new FragmentTab5();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tab1 = actionBar.newTab().setText("All");
        tab2 = actionBar.newTab().setText("Happening");
        tab3 = actionBar.newTab().setText("Within 5 Hours");
        tab4 = actionBar.newTab().setText("Future");
        tab5 = actionBar.newTab().setText("My Events");

        tab1.setTabListener(new FragmentTab1.EventsTabListener(fragmentTab1));
        tab2.setTabListener(new FragmentTab2.EventsTabListener(fragmentTab2));
        tab3.setTabListener(new FragmentTab3.EventsTabListener(fragmentTab3));
        tab4.setTabListener(new FragmentTab4.EventsTabListener(fragmentTab4));
        tab5.setTabListener(new FragmentTab5.EventsTabListener(fragmentTab5));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);
        actionBar.addTab(tab4);
        actionBar.addTab(tab5);
    }
}