package Controller;

import View.View;
import Event.Event;
import Performance.Performance;

import java.util.ArrayList;
import java.util.Collection;

public class EventPerformanceController extends Controller {
    private long nextEventID;
    private long nextPerformanceID;
    private Collection<Event> events;
    private Collection<Performance> performances;

    public EventPerformanceController(View view) {
        super(view);
        this.nextEventID = 1;
        this.nextPerformanceID = 1;
        this.events = new ArrayList<>();
        this.performances = new ArrayList<>();
    }

    
}
