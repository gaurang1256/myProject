package team5_project.cs442.eventorganizer.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;

import team5_project.cs442.eventorganizer.activities.DetailActivity;
import team5_project.cs442.eventorganizer.activities.MyEventDetailActivity;
import team5_project.cs442.eventorganizer.activities.UpdateActivity;

public class AddToCalendarTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "AddToCalendarTask";

    private UpdateActivity updateActivity = null;
    private DetailActivity deatilActivity = null;
    private MyEventDetailActivity myeventdeatilActivity = null;
    private Event event = null;
    private Calendar service = null;
    private Exception error = null;
    private final boolean isUpdateEnable;

    public AddToCalendarTask(UpdateActivity activity, GoogleAccountCredential credential, Event event) {
        this.updateActivity = activity;
        this.event = event;
        this.isUpdateEnable = true;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        service = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("IIT Event Organizer")
                .build();
    }

    public AddToCalendarTask(DetailActivity activity, GoogleAccountCredential credential, Event event) {
        this.deatilActivity = activity;
        this.event = event;
        this.isUpdateEnable = false;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        service = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("IIT Event Organizer")
                .build();
    }

    public AddToCalendarTask(MyEventDetailActivity activity, GoogleAccountCredential credential, Event event) {
        this.myeventdeatilActivity = activity;
        this.event = event;
        this.isUpdateEnable = true;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        service = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("IIT Event Organizer")
                .build();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            return uploadToApi();
        } catch (IOException e) {
            error = e;
            cancel(true);
            return null;
        }
    }

    private Void uploadToApi() throws IOException {
        Log.v(TAG, "Uploading to API");
        service.events().insert("primary", event).execute();
        Log.v(TAG, "Completed uploading to API");
        return null;
    }

    @Override
    protected void onCancelled() {
        if (error != null) {
            if (error instanceof GooglePlayServicesAvailabilityIOException) {
                // TODO: Show error
                error.printStackTrace();
            } else if (error instanceof UserRecoverableAuthIOException) {
                if (isUpdateEnable) {
                    updateActivity.startActivityForResult(
                            ((UserRecoverableAuthIOException) error).getIntent(),
                            UpdateActivity.REQUEST_AUTHORIZATION);
                } else {
                    deatilActivity.startActivityForResult(
                            ((UserRecoverableAuthIOException) error).getIntent(),
                            UpdateActivity.REQUEST_AUTHORIZATION);
                }
            } else {
                // TODO: Show error
                error.printStackTrace();
            }
        } else {
            // TODO: Show error
        }
    }
}
