package Controller;

import User.Student;
import User.StudentPreferences;
import View.View;
import Event.Event;
import Performance.Performance;
import Event.EventType;

import java.time.LocalDateTime;
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

    // Allows an authenticating user to search for performances on a given date.
    // For students with preferences, matching events are shown first.

    public void searchforPerformances(){
        if (getCurrentUser() == null) {
            getView().displayError("You must be logged in to search for performances");
            return;
        }

        LocalDateTime targetDate;
        while (true) {
            String input = getView().getInput("Enter a date to search for performances (dd-mm-yyyy): ");
            try {
                targetDate = LocalDateTime.parse(input.trim() + "T00:00:00"); // assuming date-only input
                break; // valid date, exit the loop
            } catch (Exception e) {
                getView().displayError("Invalid date format. Please use dd-mm-yyyy");
            }
        }

        Collection<Performance> performancesOnDate = new ArrayList<>();
        for (Performance p: performances) {
            if (p.getStartDateTime().toLocalDate().equals(targetDate.toLocalDate())) {
                performancesOnDate.add(p);
            }
        }

        if (performancesOnDate.isEmpty()) {
            getView().displayError("No performances found on " + targetDate.toLocalDate());
            return;
        }

        // if the user is a student with preferences, sort performances ot match preferences first
        if (getCurrentUser() instanceof Student) {
            Student student = (Student) getCurrentUser();
            if (student.getStudentPreferences() != null) { // check for preferences
                Collection<Performance> matching = new ArrayList<>();
                Collection<Performance> nonMatching = new ArrayList<>();

                for (Performance p : performancesOnDate) {
                    EventType type  = p.getEvent().getType();
                    boolean matches = false;
                    StudentPreferences prefs = student.getStudentPreferences();

                    if (prefs.getPreferDanceEvents() && type == EventType.Theatre) {


                    if (matches) {
                        matching.add(p);
                    } else {
                        nonMatching.add(p);
                    }
                }

                performancesOnDate.clear();
                performancesOnDate.addAll(matching);
                performancesOnDate.addAll(nonMatching);
            }

        }

        for (Performance p: performancesOnDate) {
            getView().displaySuccess(p.toString());
        }

    }
}
