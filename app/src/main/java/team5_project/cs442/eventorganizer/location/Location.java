package team5_project.cs442.eventorganizer.location;

import com.google.android.gms.maps.model.BitmapDescriptor;

import java.util.ArrayList;
import java.util.List;

import team5_project.cs442.eventorganizer.event.Event;

/**
 * Created by sangwon on 10/26/15.
 */
public class Location {

    private String mLocation;

    private Double mLatitude;

    private Double mLongitude;

    private List<Event> events;

    private BitmapDescriptor icon;

    public Location(String mLocation, Double mLatitude, Double mLongitude) {
        this.mLocation = mLocation;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        events = new ArrayList<Event>();
        icon = null;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public boolean setEvents(List<Event> _events) {
        events = _events;
        return this.events != null;
    }

    public boolean addEvent(Event event) {
        return (event != null) ? (this.events.add(event)) : false;
    }

    public boolean updateEvent(Event _event) {
        for(Event event : events) {
            if(event.getmEventId() == _event.getmEventId()) {
                int index = events.indexOf(event);
                events.add(index, _event);
                return true;
            }
        }
        return false;
    }

    public boolean deleteEvent(Event _event) {
        for(Event event : events) {
            if(event.getmEventId() == _event.getmEventId()) {
                int index = events.indexOf(event);
                events.remove(index);
                return true;
            }
        }
        return events.contains(_event);
    }


    public BitmapDescriptor getIcon() {
        return icon;
    }

    public List<Event> getEvents() {
        return (events != null) ? events : null;
    }

    public int getEventsCounter() {
        return events.size();
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }
}
