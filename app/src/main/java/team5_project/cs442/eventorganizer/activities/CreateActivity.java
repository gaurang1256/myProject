package team5_project.cs442.eventorganizer.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.plus.Plus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import team5_project.cs442.eventorganizer.R;
import team5_project.cs442.eventorganizer.database.Database;
import team5_project.cs442.eventorganizer.event.Event;
import team5_project.cs442.eventorganizer.event.EventTimeChecker;
import team5_project.cs442.eventorganizer.location.LocationLoader;

public class CreateActivity extends BaseActivity implements View.OnClickListener {

    private SimpleDateFormat dateformat;
    private SimpleDateFormat timeformat;

    EditText mEditName;
    Spinner mSpinnerLoc;
    TextView mTextStartDate;
    TextView mTextStartTime;
    TextView mTextEndDate;
    TextView mTextEndTime;
    EditText mEditDesc;
    EditText mEditHost;
    EditText mEditEmail;
    EditText mEditCost;
    Button mBtnCreate;

    private DatePickerDialog mStartDateDialog;
    private TimePickerDialog mStartTimeDialog;
    private DatePickerDialog mEndDateDialog;
    private TimePickerDialog mEndTimeDialog;

    private Calendar mStartCal;
    private Calendar mEndCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mEditName = (EditText)findViewById(R.id.editCreateName);

        initLocationSpinner();

        mEditDesc = (EditText)findViewById(R.id.editCreateDesc);
        mEditHost = (EditText)findViewById(R.id.editCreateHost);

        mEditEmail = (EditText)findViewById(R.id.editCreateEmail);
        mEditEmail.setText(email);
        mEditEmail.setEnabled(false);

        mEditCost = (EditText)findViewById(R.id.editCreateCost);

        mBtnCreate = (Button)findViewById(R.id.btnCreate);
        mBtnCreate.setOnClickListener(this);

        initDateTimePickers();
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
        } else if (v == mBtnCreate) {
            createEvent();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void createEvent() {
        String name = mEditName.getText().toString();
        String loc = mSpinnerLoc.getSelectedItem().toString();


        Date start = mStartCal.getTime();
        String sDate = EventTimeChecker.formatter.format(start);
        Date startDate = new Date(sDate);

        Date end = mEndCal.getTime();
        String eDate = EventTimeChecker.formatter.format(end);
        Date endDate = new Date(eDate);

        String desc = mEditDesc.getText().toString();
        String host = mEditHost.getText().toString();
        String _cost = mEditCost.getText().toString();
        double cost;
        if ((_cost == null) || (_cost.equals("")) || (_cost.length() == 0)) {
            cost = 0.0d;
        } else {
            cost = Double.parseDouble(_cost);
        }


        if(EventTimeChecker.isStartTimeBiggerThanEndTime(startDate, endDate)) {
            Event event = new Event(0, name, loc, startDate, endDate, desc, host, email, cost);
            Database.insert(event);

            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Event's END TIME should be later than START TIME!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initLocationSpinner() {
        mSpinnerLoc = (Spinner)findViewById(R.id.spinnerCreateLoc);
        List<String> locations = LocationLoader.getLocationNames();
        ArrayAdapter<String> adaptLoc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        mSpinnerLoc.setAdapter(adaptLoc);
    }

    private void initDateTimePickers() {
        dateformat = new SimpleDateFormat("E MMM dd, yyyy");
        timeformat = new SimpleDateFormat("hh:mm a");

        mTextStartDate = (TextView)findViewById(R.id.editCreateStartDate);
        mTextStartTime = (TextView)findViewById(R.id.editCreateStartTime);
        mTextEndDate = (TextView)findViewById(R.id.editCreateEndDate);
        mTextEndTime = (TextView)findViewById(R.id.editCreateEndTime);



        mStartCal = Calendar.getInstance();

        mTextStartDate.setText(dateformat.format(mStartCal.getTime()));
        mTextStartDate.setOnClickListener(this);

        mTextStartTime.setText(timeformat.format(mStartCal.getTime()));
        mTextStartTime.setOnClickListener(this);

        mEndCal = Calendar.getInstance();

        mTextEndDate.setText(dateformat.format(mEndCal.getTime()));
        mTextEndDate.setOnClickListener(this);

        mTextEndTime.setText(timeformat.format(mEndCal.getTime()));
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
}
