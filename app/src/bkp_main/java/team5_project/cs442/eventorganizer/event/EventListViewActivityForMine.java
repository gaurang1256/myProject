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

/**
 * Created by sangwon on 10/23/15.
 */
public class EventListViewActivityForMine extends Activity {

    public List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flag_for_mine);
        ListView list = (ListView) findViewById(R.id.my_flag_list_view_activity);

        int resID = R.layout.my_event_info_list_view;
        Bundle bundle = getIntent().getExtras();
        events = (List<Event>) bundle.getSerializable("Events");
        EventAdapterForMyList eventAdapterForEventInfo = new EventAdapterForMyList(this, resID, events);
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