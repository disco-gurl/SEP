package Event;

import Performance.Performance;
import java.util.Collection;
import User.EntertainmentProvider;
import java.util.ArrayList;

public class Event {
    private long eventID;
    private String title;
    private EventType type;
    private boolean isTicketed;
    private Collection<Performance> performances;
    private EntertainmentProvider organiser;

    public Performance getPerformanceByID(long performanceID) {
        for (Performance p : performances) {
            if (p.getID() == performanceID) {
                return p;
            }
        }
        return null;
    }

    public boolean getIsTicketed() {
        return isTicketed;
    }

    public String getOrganiserEmail() {
        return organiser.getEmail();
    }

    public String getOrganiserName() {
        return organiser.getName();
    }

    public String getTitle() {
        return title;
    }

    public long getEventID() {
        return eventID;
    }

    public EventType getType() {
        return type;
    }

    public Collection<Performance> getPerformances() {
        return performances;
    }


}
