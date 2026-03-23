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

    /**
     * Using an ID finds a performance accordingly.
     *
     * @param performanceID
     * @return
     */
    private Performance getPerformanceByID(long performanceID) {
        for (Performance p : performances) {
            if (p.getID() == performanceID) {
                return p;
            }
        }
        return null;
    }

    /**
     *
     */
    public void viewPerformance() {
        if (getCurrentUser() == null) {
            getView().displayError("You must be logged in to view a performance");
            return;
        }

        // get the performance ID for the specific performance.
        String input = getView().getInput("Enter performance ID: ");

        // parse it as a number or send back error to display if unable.
        long performanceID;
        try {
            performanceID = Long.parseLong(input.trim());
        }   catch (NumberFormatException e) {
            getView().displayError("Invalid format");
            return;
        }

        Performance performance = getPerformanceByID(performanceID);

        if (performance == null) {
            getView().displayError("No performance found with performance ID" + performanceID);
            return;
        }
    }
}
