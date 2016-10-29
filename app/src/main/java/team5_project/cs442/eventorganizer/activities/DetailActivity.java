package team5_project.cs442.eventorganizer.activities;

import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import team5_project.cs442.eventorganizer.R;
import team5_project.cs442.eventorganizer.asyncTask.AddToCalendarTask;
import team5_project.cs442.eventorganizer.asyncTask.CalendarEventsTask;
import team5_project.cs442.eventorganizer.database.Database;
import team5_project.cs442.eventorganizer.event.Event;
import team5_project.cs442.eventorganizer.event.EventTimeChecker;
import team5_project.cs442.eventorganizer.location.LocationLoader;

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private static final String TAG = "UpdateActivity";

    private GoogleAccountCredential credential;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private SimpleDateFormat dateformat;
    private SimpleDateFormat timeformat;

    private TextView mEditName;
    private TextView mSpinnerLoc;
    private TextView mTextStartDate;
    private TextView mTextStartTime;
    private TextView mTextEndDate;
    private TextView mTextEndTime;
    private TextView mEditDesc;
    private TextView mEditHost;
    private TextView mEditCost;

    private Button mBtnAddToCalendar;

    private DatePickerDialog mStartDateDialog;
    private TimePickerDialog mStartTimeDialog;
    private DatePickerDialog mEndDateDialog;
    private TimePickerDialog mEndTimeDialog;

    private Calendar mStartCal;
    private Calendar mEndCal;

    private Event mEvent;

    private boolean following = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mEvent = (Event) intent.getSerializableExtra("Event");

        mEditName = (TextView) findViewById(R.id.editUpdateName);
        mSpinnerLoc = (TextView) findViewById(R.id.spinnerUpdateLoc);
        mTextStartDate = (TextView) findViewById(R.id.editUpdateStartDate);
        mTextStartTime = (TextView) findViewById(R.id.editUpdateStartTime);
        mTextEndDate = (TextView) findViewById(R.id.editUpdateEndDate);
        mTextEndTime = (TextView) findViewById(R.id.editUpdateEndTime);
        mEditDesc = (TextView) findViewById(R.id.editUpdateDesc);
        mEditHost = (TextView) findViewById(R.id.editUpdateHost);
        mEditCost = (TextView) findViewById(R.id.editUpdateCost);
        mBtnAddToCalendar = (Button) findViewById(R.id.btnAddToCalendar);

        mEditName.setText(mEvent.getmEventName()+"\n");
        mSpinnerLoc.setText(mEvent.getmEventLocation() + "\n");
        initDates();
        mEditDesc.setText(mEvent.getmDescription()+"\n");
        mEditHost.setText(mEvent.getmHost()+"\n");

        if(mEvent.getmCost() != 0) {
            DecimalFormat df = new DecimalFormat("$#.00");
            mEditCost.setText(String.valueOf(df.format(mEvent.getmCost())));
        } else  mEditCost.setText("FREE");

        TextView mEditEmail = (TextView) findViewById(R.id.editUpdateEmail);
        mEditEmail.setText(mEvent.getmEventCreator());
        mEditEmail.setEnabled(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mEditName.setEnabled(false);
        mSpinnerLoc.setEnabled(false);
        mTextStartDate.setEnabled(false);
        mTextStartTime.setEnabled(false);
        mTextEndDate.setEnabled(false);
        mTextEndTime.setEnabled(false);
        mEditDesc.setEnabled(false);
        mEditHost.setEnabled(false);
        mEditCost.setEnabled(false);


        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(preferences.getString(PREF_ACCOUNT_NAME, LoginActivity.accoutname));

        mBtnAddToCalendar.setEnabled(false);
        mBtnAddToCalendar.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v(TAG, "Resume");

        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            // TODO: Show error
        }

    }

    private void refreshCalendar() {
        if (following && !mBtnAddToCalendar.getText().equals("Already Added")) {
            mBtnAddToCalendar.setEnabled(false);
            mBtnAddToCalendar.setText("Already Added");
            mBtnAddToCalendar.setBackgroundColor(getApplication().getResources().getColor(R.color.grey));
        } else if(!mBtnAddToCalendar.getText().equals("Add to Calender")){
            mBtnAddToCalendar.setEnabled(true);
            mBtnAddToCalendar.setText("Add to Calendar");
            mBtnAddToCalendar.setBackgroundColor(getApplication().getResources().getColor(R.color.red));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "Activity result");

        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // TODO: Show error
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;

            case REQUEST_GOOGLE_PLAY_SERVICES:
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == mTextStartDate) {
            mStartDateDialog.show();
        } else if (v == mTextStartTime) {
            mStartTimeDialog.show();
        } else if (v == mTextEndDate) {
            mEndDateDialog.show();
        } else if (v == mTextEndTime) {
            mEndTimeDialog.show();
        } else if (v == mBtnAddToCalendar) {
            addToCalendar();
        }
    }

    private void addToCalendar() {
        new AddToCalendarTask(this, credential, buildCalendarEvent()) {
            @Override
            protected void onPostExecute(Void result) {
                following = true;
                refreshCalendar();
            }
        }.execute();
        mBtnAddToCalendar.setEnabled(false);
    }

    private com.google.api.services.calendar.model.Event buildCalendarEvent() {

        com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event();

        event.setSummary(mEvent.getmEventName());
        event.setLocation(mEvent.getmEventLocation());
        event.setStart(new EventDateTime().setDateTime(new DateTime(mEvent.getmEventStartTime())));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(mEvent.getmEventEndTime())));
        event.setDescription(mEvent.getmDescription());
        event.setOrganizer(new com.google.api.services.calendar.model.Event.Organizer().setDisplayName(mEvent.getmHost()));
        event.setCreator(new com.google.api.services.calendar.model.Event.Creator().setEmail(mEvent.getmEventCreator()));

        return event;
    }

    private void updateEvent() {
        mEvent.setmEventName(mEditName.getText().toString());
        mEvent.setmEventLocation(mSpinnerLoc.getText().toString());

        Date start = mStartCal.getTime();
        String sDate = EventTimeChecker.formatter.format(start);
        Date startDate = new Date(sDate);

        mEvent.setmEventStartTime(startDate);

        Date end = mEndCal.getTime();
        String eDate = EventTimeChecker.formatter.format(end);
        Date endDate = new Date(eDate);

        mEvent.setmEventEndTime(endDate);

        //mEvent.setmEventStartTime(mStartCal.getTime());
        //mEvent.setmEventEndTime(mEndCal.getTime());
        mEvent.setmDescription(mEditDesc.getText().toString());
        mEvent.setmHost(mEditHost.getText().toString());
        if (mEditCost.getText().toString() != null) {
            mEvent.setmCost(Double.parseDouble(mEditCost.getText().toString()));
        } else {
            mEvent.setmCost(0.0d);
        }

        Database.update(mEvent);

        finish();
    }

    private void initLocation() {
        List<String> locations = LocationLoader.getLocationNames();

        int index = locations.indexOf(mEvent.getmEventLocation());
        if (index < 0) index = 0;

        ArrayAdapter<String> adaptLoc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        //mSpinnerLoc.setAdapter(adaptLoc);

//        mSpinnerLoc.setSelection(index);
    }

    private void initDates() {
        dateformat = new SimpleDateFormat("E MMM dd, yyyy");
        timeformat = new SimpleDateFormat("hh:mm a");

        mStartCal = Calendar.getInstance();
        mEndCal = Calendar.getInstance();

        mStartCal.setTime(mEvent.getmEventStartTime());
        mEndCal.setTime(mEvent.getmEventEndTime());

        mTextStartDate.setText(dateformat.format(mStartCal.getTime()));
        mTextStartTime.setText(timeformat.format(mStartCal.getTime())+"\n");
        mTextEndDate.setText(dateformat.format(mEndCal.getTime()));
        mTextEndTime.setText(timeformat.format(mEndCal.getTime())+"\n");
    }

    private void initEditDate() {
        mTextStartDate.setOnClickListener(this);
        mTextStartTime.setOnClickListener(this);
        mTextEndDate.setOnClickListener(this);
        mTextEndTime.setOnClickListener(this);

        mStartDateDialog = createDatePickerDialog(mTextStartDate, mStartCal);
        mStartTimeDialog = createTimePickerDialog(mTextStartTime, mStartCal);
        mEndDateDialog = createDatePickerDialog(mTextEndDate, mEndCal);
        mEndTimeDialog = createTimePickerDialog(mTextEndTime, mEndCal);
    }

    private DatePickerDialog createDatePickerDialog(final TextView text, final Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        changeDate(text, cal, y, m, d);
                    }
                }, year, month, day);
    }

    private TimePickerDialog createTimePickerDialog(final TextView text, final Calendar cal) {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        return new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int h, int m) {
                        changeTime(text, cal, h, m);
                    }
                }, hour, minute, false);
    }

    private void changeDate(TextView text, Calendar cal, int year, int month, int day) {
        cal.set(year, month, day);
        text.setText(dateformat.format(cal.getTime()));
    }

    private void changeTime(TextView text, Calendar cal, int hour, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        text.setText(timeformat.format(cal.getTime()));
    }

    private void refreshResults() {
        Log.v(TAG, "Refresh results");

        if (credential.getSelectedAccountName() == null) {
            chooseAccount();

        } else {
            Log.v(TAG, "Selected account name = " + credential.getSelectedAccountName());

            if (isDeviceOnline()) {
                new CalendarEventsTask(this, credential) {
                    @Override
                    protected void onPostExecute(List<String> result) {
                        following = false;
                        if (result != null) {

                            for (String name : result) {
                                if ((name != null) && (name.equals(mEvent.getmEventName()))) {
                                    following = true;
                                    break;
                                }
                            }
                        }
                        refreshCalendar();
                    }
                }.execute();

            } else {
                // TODO: Show error
            }
        }
    }

    private void chooseAccount() {
        Log.v(TAG, "Choosing account");

        Intent intent = credential.newChooseAccountIntent();

        startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            // TODO: Show error
            return false;
        } else if (status != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     @Override public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
     case R.id.sign_out:
     Plus.AccountApi.clearDefaultAccount(LoginActivity.mGoogleApiClient);
     Plus.AccountApi.revokeAccessAndDisconnect(LoginActivity.mGoogleApiClient);
     LoginActivity.mGoogleApiClient.disconnect();
     finish();
     return true;
     default:
     return super.onOptionsItemSelected(item);
     }
     }
     */

}