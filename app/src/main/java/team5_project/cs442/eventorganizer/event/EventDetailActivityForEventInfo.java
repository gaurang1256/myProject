package team5_project.cs442.eventorganizer.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import team5_project.cs442.eventorganizer.activities.BaseActivity;
import team5_project.cs442.eventorganizer.R;

/** OBSOLETE?????????????????????????!!!!!!!!!!!!
 * Created by sangwon on 10/23/15.
 */
public class EventDetailActivityForEventInfo extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_form);

        Intent i = getIntent();
        Event event = (Event) i.getSerializableExtra("Event");

        final EditText eventName = (EditText) findViewById(R.id.event_name_edit);
        final EditText eventLocation = (EditText) findViewById(R.id.event_location_edit);
        final EditText eventStartTime = (EditText) findViewById(R.id.event_start_time_edit);
        final EditText eventEndTime = (EditText) findViewById(R.id.event_end_time_edit);
        final EditText eventHost = (EditText) findViewById(R.id.event_host_edit);
        final EditText eventOrganizer = (EditText) findViewById(R.id.event_organizer_edit);
        final EditText eventCost = (EditText) findViewById(R.id.event_cost_edit);

        final Button goBackButton = (Button) findViewById(R.id.go_back);


        eventName.setText(event.getmEventName());
        eventLocation.setText(event.getmEventLocation());
        eventStartTime.setText(String.valueOf(event.getmEventStartTime()));
        eventEndTime.setText(String.valueOf(event.getmEventEndTime()));
        eventHost.setText(event.getmHost());
        eventOrganizer.setText(event.getmEventCreator());
        eventCost.setText("$" + String.valueOf(event.getmCost()));



        // Here we need to compare seesion w/ event Creator to disable all edit texts.
        // For now.. all disabled..

        eventName.setEnabled(false);
        eventLocation.setEnabled(false);
        eventStartTime.setEnabled(false);
        eventEndTime.setEnabled(false);
        eventHost.setEnabled(false);
        eventOrganizer.setEnabled(false);
        eventCost.setEnabled(false);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}