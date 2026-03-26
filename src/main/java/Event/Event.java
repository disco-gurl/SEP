package Event;

import Performance.Performance;
import java.util.Collection;
import User.EntertainmentProvider;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Event {
    private long eventID;
    private String title;
    private EventType type;
    private boolean isTicketed;
    private Collection<Performance> performances;
    private EntertainmentProvider organiser;

    // constructor for creating an event
    public Event(long eventID, String title, EventType type, boolean isTicketed, EntertainmentProvider organiser) {

        this.eventID = eventID;
        this.title = title;
        this.type = type;
        this.isTicketed = isTicketed;
        this.performances = new ArrayList<>();
        this.organiser = organiser;

    }

    // creates a performance and adds it to the event
    public Performance createPerformance(long performanceID, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                         Collection<String> performerNames, String venueAddress, int venueCapacity,
                                         boolean venueIsOutdoors, boolean venueAllowsSmoking,
                                         int numTickets, double ticketPrice) {

        Performance p = new Performance(performanceID, startDateTime, endDateTime,
                performerNames, venueAddress, venueCapacity, venueIsOutdoors, venueAllowsSmoking,
                numTickets, ticketPrice, this);

        performances.add(p);

        return p;
    }

    // checks if there is already a performance happening at the same time
    public boolean hasPerformanceAtSameTimes(LocalDateTime startDateTime, LocalDateTime endDateTime) {

        for (Performance p : performances) {

            // overlap check - if one starts before the other ends
            if (p.getStartDateTime().isBefore(endDateTime) && startDateTime.isBefore(p.getEndDateTime())) {
                return true;

            }
        }
        return false;
    }

    public void addPerformance(Performance p) {
        performances.add(p);
    }

    public EntertainmentProvider getOrganiser() {
        return organiser;
    }



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

    /**
     * Method calculates the average rating across all the performances
     *
     * @return
     */
    public double getAverageRatingOfPerformances() {
        int totalRatings = 0;
        int count = 0;

        for (Performance p : performances) {
            for (int rating : p.getReviewRatings()) {
                totalRatings += rating;
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }
        return (double) totalRatings / count;
    }

    /**
     * Returns all individual reviews across all
     * performances of this event.
     */
    public Collection<String> getAllPerformanceReviews() {
        Collection<String> allReviews = new ArrayList<>();

        // Go through each performance of this event
        for (Performance p : performances) {

            // Get ratings and comments as arrays so can match them by position
            Integer[] ratings = p.getReviewRatings().toArray(new Integer[0]);
            String[] comments = p.getReviewComments().toArray(new String[0]);

            // Go through each review for this performance
            for (int i = 0; i < ratings.length; i++) {

                String review = "Performance " + p.getID() + " - Rating: " + ratings[i];

                // Add the comment if it exists with checks.
                boolean hasComment = i < comments.length && comments[i] != null && !comments[i].isEmpty();

                if (hasComment) {
                    review += " - Comment: " + comments[i];
                }

                allReviews.add(review);
            }
        }

        return allReviews;
    }

    public String toString() {

        return "Event ID: " + eventID
                + "\nTitle: " + title
                + "\nType: " + type
                + "\nTicketed: " + isTicketed
                + "\nOrganiser: " + organiser.getOrgName();
    }

}