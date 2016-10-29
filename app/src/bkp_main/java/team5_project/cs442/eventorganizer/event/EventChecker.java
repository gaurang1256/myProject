package team5_project.cs442.eventorganizer.event;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sangwon on 10/26/15.
 */
public class EventChecker {

    private static DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    private static TimeZone obj = TimeZone.getTimeZone("CST");

    public static BitmapDescriptor eventChecker(final Date _startEvent, final Date _endEvent) {
        /**
         * We can use this method to differenciate for time..
         */
        // formatter.setTimeZone(obj);
        String _currentTime = EventChecker.formatter.format(new Date());

        Date currentTime = new Date(_currentTime);

        // currentTime = Main.formatter.parse(Main.currentdate.getTime().toString());

        System.out.println(currentTime.toString() + " : " + _startEvent.toString() + " : " + _endEvent.toString());
        long diff = _startEvent.getTime() - currentTime.getTime();

        // String currentDateTime = formatter.format(currentdate.getTime());
        // String startEvent = formatter.format(_startEvent);
        // String endEvent = formatter.format(_endEvent);

        int diffhours = (int) (diff / (60 * 60 * 1000));
        System.out.println(diffhours);
		/*
		 * Assign green flag when current time is the same as event's start time, or between the event's start and end
		 * time. Assign yellow flag when current time is 2 hours or less away from event's start time. Assign grey flag
		 * when current time is more than 2 hours away from event time.
		 */
        if ( currentTime.equals(_startEvent) || (currentTime.after(_startEvent) && currentTime.before(_endEvent)) ) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }
        if ( (diffhours <= 2) && (diffhours > 0) ) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        }
    }

}
