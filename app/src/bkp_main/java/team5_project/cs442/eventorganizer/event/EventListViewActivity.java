package team5_project.cs442.eventorganizer.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import team5_project.cs442.eventorganizer.R;
import team5_project.cs442.eventorganizer.activities.EventAdapterForListViewActivity;

/**
 * Created by sangwon on 10/23/15.
 */
public class EventListViewActivity extends Activity {

    public List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flag_for_activity);
        ListView list = (ListView) findViewById(R.id.flag_list_view_activity);

        int resID = R.layout.event_info_list_view;
        //Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        Log.d("Listvew acti:", String.valueOf(bundle.isEmpty()));
        //Eventppter infoAdapter = (EventAdapter) bundle.getSerializable("EventAdapter");

        events = (List<Event>) bundle.getSerializable("Events");
        EventAdapterForListViewActivity eventAdapterForEventInfo = new EventAdapterForListViewActivity(this, resID, events);
        list.setAdapter(eventAdapterForEventInfo);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), EventDetailActivityForEventInfo.class);
                Event event = events.get(position);
                i.putExtra("Event", event);
                startActivity(i);
            }

        });

    }

}