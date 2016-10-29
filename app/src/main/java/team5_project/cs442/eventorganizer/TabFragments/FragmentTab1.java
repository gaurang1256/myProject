package team5_project.cs442.eventorganizer.TabFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import team5_project.cs442.eventorganizer.R;
import team5_project.cs442.eventorganizer.activities.BaseActivity;
import team5_project.cs442.eventorganizer.activities.DetailActivity;
import team5_project.cs442.eventorganizer.activities.EventAdapterForListViewActivity;
import team5_project.cs442.eventorganizer.activities.TabActivity;
import team5_project.cs442.eventorganizer.activities.UpdateActivity;
import team5_project.cs442.eventorganizer.database.Database;
import team5_project.cs442.eventorganizer.event.Event;
import team5_project.cs442.eventorganizer.event.EventTimeChecker;
import team5_project.cs442.eventorganizer.event.Tuple;

/**
 * Created by Anna Eng on 11/20/2015.
 */
public class FragmentTab1 extends Fragment {
    /**
     * EventType
     * 0 = All
     * 1 = Ongoing
     * 2 = Upcoming in 5 hours
     * 3 = Future (After 5 hours)
     * 4 = Mine
     */
    private int eventType = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab, container, false);

        int resID = R.layout.event_info_list_view;
        List<Event> tempEvents;

        tempEvents = Database.readList();
        tempEvents = EventTimeChecker.isRightEventTypes(tempEvents, eventType);

        final List<Event> events = tempEvents;

        EventAdapterForListViewActivity eventAdapterForEventInfo = new EventAdapterForListViewActivity(getActivity(), resID, events);

        ListView list = (ListView) rootView.findViewById(R.id.tablistview);
        list.setAdapter(eventAdapterForEventInfo);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                Intent i =  new Intent(getActivity().getBaseContext(), DetailActivity.class);

                Event event = events.get(position);
                i.putExtra("Event", event);
                startActivity(i);
            }
        });

        return rootView;
    }

        /*public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab, container, false);
        TextView textview = (TextView) view.findViewById(R.id.tabtextview);
        textview.setText("Tab 2");
        return view;
    }*/

    public static class EventsTabListener implements android.app.ActionBar.TabListener {
        Fragment fragment;

        public EventsTabListener(Fragment fragment) {
            this.fragment = fragment;
        }

        public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            ft.replace(R.id.fragment_container, fragment);
        }

        public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            ft.remove(fragment);
        }

        public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            // nothing done here
        }
    }
}
