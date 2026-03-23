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

    public Collection<String> getAllPerformanceReviews() {
        Collection<String> allReviews = new ArrayList<>();

        for (Performance p : performances) {
            Integer[] ratings = p.getReviewRatings().toArray(new Integer[0]);
            String[] comments = p.getReviewComments().toArray(new String[0]);

            for (Performance p: performances) {
            for (int i = 0; i < ratings.length; i++) {
                String review = "Performance " + p.getID() + " - Rating: " + ratings[i];
                if (i < comments.length && comments[i] != null && !comments[i].isEmpty()) {
                    review += " - Comment: " + comments[i];
                }
                allReviews.add(review);
            }
        }
        return allReviews;
        }
    }
}
