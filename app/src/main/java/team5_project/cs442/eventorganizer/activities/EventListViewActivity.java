package team5_project.cs442.eventorganizer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import team5_project.cs442.eventorganizer.R;
import team5_project.cs442.eventorganizer.database.Database;
import team5_project.cs442.eventorganizer.event.Event;
import team5_project.cs442.eventorganizer.event.EventTimeChecker;
import team5_project.cs442.eventorganizer.event.Tuple;

/**
 * Created by sangwon on 10/23/15.
 */
public class EventListViewActivity extends BaseActivity {

    private Tuple mTuple;

    private Spinner mSpinnerLoc;

    private TextView mEventTypeText;
    /**
     * EventType
     * 0 = All
     * 1 = Ongoing
     * 2 = Upcoming in 5 hours
     * 3 = Future (After 5 hours)
     * 4 = Mine
     */
    private int eventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flag_for_activity);

        eventType = 0;

        Bundle bundle = getIntent().getExtras();
        mTuple = (Tuple) bundle.getSerializable("Tuple");
        mEventTypeText = (TextView) findViewById(R.id.eventType);

                mSpinnerLoc = (Spinner) findViewById(R.id.even_type_spinner);

          /*  mSpinnerLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                      public void onItemSelected(AdapterView<?> parent, View view,
                                                                                                  int position, long id) {
                                   eventType = position;
                                    setmEventTypeText(eventType);
                                        onResume();
                                    }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                        eventType = 0;
                                        setmEventTypeText(eventType);
                                        onResume();
                                    }
                            });*/
        if (mTuple.getKey().equals(Database.TAG_LOC)) {
            mSpinnerLoc.setVisibility(View.GONE);
            mEventTypeText.setText("Events at " + mTuple.getValue());
        } else {

            mSpinnerLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    eventType = position;
                    setmEventTypeText(eventType);
                    onResume();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    eventType = 0;
                    setmEventTypeText(eventType);
                    onResume();
                }
            });
        }
        listInstance = this;
    }

    private void setmEventTypeText(int eventType) {
        switch (eventType) {
            case 1:
                mEventTypeText.setText("Ongoing Events List :");
                return;
            case 2:
                mEventTypeText.setText("Events In 5 hours List :");
                return;
            case 3:
                mEventTypeText.setText("Events After 5 hours List :");
                return;
            case 4:
                mEventTypeText.setText("My Events List :");
                return;
            default:
                mEventTypeText.setText("All Events List :");
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int resID = R.layout.event_info_list_view;
        List<Event> tempEvents;
        if (eventType == 4 || (mTuple.getKey().equals(Database.TAG_LOC))) {
            tempEvents = Database.readList(mTuple.getKey(), mTuple.getValue());
        } else {
            tempEvents = Database.readList();
            tempEvents = EventTimeChecker.isRightEventTypes(tempEvents, eventType);
        }

        final List<Event> events = tempEvents;

        EventAdapterForListViewActivity eventAdapterForEventInfo = new EventAdapterForListViewActivity(this, resID, events);

        ListView list = (ListView) findViewById(R.id.flag_list_view_activity);
        list.setAdapter(eventAdapterForEventInfo);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = null;
               /* if(eventType == 4) {
                    i = new Intent(getBaseContext(), UpdateActivity.class);
                } else {*/
                    i = new Intent(getBaseContext(), DetailActivity.class);
             //   }
                Event event = events.get(position);
                i.putExtra("Event", event);
                startActivity(i);
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}