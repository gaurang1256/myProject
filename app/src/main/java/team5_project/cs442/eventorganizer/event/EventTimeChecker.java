package team5_project.cs442.eventorganizer.event;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sangwon on 10/26/15.
 */
public class EventTimeChecker {

    public static DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public static Calendar currentdate = Calendar.getInstance();

    private static String _currentTime;

    private static Date currentTime;


    private static double getDiffHoursWithCurrentHours(final Date _startEvent) {

        _currentTime = EventTimeChecker.formatter.format(currentdate.getTime());
        currentTime = new Date(_currentTime);

        long diff = _startEvent.getTime() - currentTime.getTime();

        double _diffhours = (diff / (60 * 60 * 1000));
        return _diffhours;
    }

    public static BitmapDescriptor eventChecker(final Date _startEvent, final Date _endEvent) {
        /**
         * We can use this method to differenciate for time..
         */

        double diffhours = getDiffHoursWithCurrentHours(_startEvent);
        if ((currentTime.after(_startEvent) && currentTime.before(_endEvent))) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }
        if ((diffhours <= 5.0d) && (diffhours >= 0.0d)) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        }
    }

    public static List<Event> isRightEventTypes(final List<Event> givenEvents, final int eventType ) {
        /**
         * Event type
         * 0 = All
         * 1 = Ongoing event
         * 2 = Upcoming event in 5 hours
         * 3 = After 5hours
         */
        if(eventType == 0) { // Just return all
            return givenEvents;
        }

        List<Event> filteredList = new ArrayList<Event>();
        for(Event event : givenEvents) {
            double diffhours = getDiffHoursWithCurrentHours(event.getmEventStartTime());

            if (((currentTime.after(event.getmEventStartTime()) && currentTime.before(event.getmEventEndTime()))) && (eventType == 1)) {
                filteredList.add(event);
                continue;
            }


            if (((diffhours <= 5.0d) && (diffhours >= 0.0d)) && (eventType == 2)) {
                filteredList.add(event);
                continue;
            }

            if ((diffhours > 5.0d)&& (eventType == 3)) {
                filteredList.add(event);
                continue;
            }
        }
        return filteredList;
    }

    public static boolean isEventPassed(final Date _startEvent, final Date _endEvent) {
        double diffhours = getDiffHoursWithCurrentHours(_startEvent);

        if ((diffhours >= 0)) {
            return false; //  Not come yet
        }
        if ((currentTime.after(_startEvent) && currentTime.before(_endEvent))) {
            return false; // Event is on going.
        }
        return true; //  Event passed, should filter

    }

    public static boolean isStartTimeBiggerThanEndTime(final Date startEvent, final Date endEvent) {
        long diff =  endEvent.getTime() - startEvent.getTime();

        double diffhours = (diff / (60 * 60 * 1000));

        return (diffhours > 0);
    }
}
