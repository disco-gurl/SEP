package Performance;

import java.time.LocalDateTime;
import java.util.Collection;
import Event.Event;
import Booking.Booking;
import java.util.ArrayList;

public class Performance {
    private long performanceId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Collection<String> performerNames;
    private String venueAddress;
    private int venueCapacity;
    private boolean venueIsOutdoors;
    private boolean venueAllowsSmoking;
    private int numTicketsTotal;
    private int numTicketsSold;
    private double ticketPrice;
    private boolean isSponsored;
    private double sponsoredAmount;
    private Collection<Integer> reviewRatings;
    private Collection<String> reviewComments;
    private PerformanceStatus status;
    private Event event;
    private Collection<Booking> bookings;

    public Performance () {
        this.numTicketsTotal = numTicketsTotal;
        this.numTicketsSold = 0;
        this.ticketPrice = ticketPrice;
        this.event = event;
        this.isSponsored = false;
        this.sponsoredAmount = 0;
    }

    public Collection<Booking> getBookings() { return bookings; }

    // constructor that takes in all the details, used when creating performances through an event
    public Performance(long performanceId, LocalDateTime startDateTime, LocalDateTime endDateTime,
                       Collection<String> performerNames, String venueAddress, int venueCapacity,
                       boolean venueIsOutdoors, boolean venueAllowsSmoking,
                       int numTicketsTotal, double ticketPrice, Event event) {

        this.performanceId = performanceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.performerNames = performerNames;
        this.venueAddress = venueAddress;
        this.venueCapacity = venueCapacity;
        this.venueIsOutdoors = venueIsOutdoors;
        this.venueAllowsSmoking = venueAllowsSmoking;
        this.numTicketsTotal = numTicketsTotal;
        this.numTicketsSold = 0;
        this.ticketPrice = ticketPrice;
        this.event = event;
        this.isSponsored = false;
        this.sponsoredAmount = 0;
        this.reviewRatings = new ArrayList<>();
        this.reviewComments = new ArrayList<>();
        this.status = PerformanceStatus.ACTIVE;
        this.bookings = new ArrayList<>();

    }

    // sponsor use case that sets the sponsored flag and amount
    public void sponsor(double amount) {

        this.isSponsored = true;
        this.sponsoredAmount = amount;

    }

    public boolean isSponsored() {
        return isSponsored;
    }

    public double getSponsoredAmount() {
        return sponsoredAmount;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public Collection<String> getPerformerNames() {
        return performerNames;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public int getVenueCapacity() {
        return venueCapacity;
    }

    public boolean isVenueOutdoors() {
        return venueIsOutdoors;
    }

    public boolean isVenueAllowsSmoking() {
        return venueAllowsSmoking;
    }

    public int getNumTicketsTotal() {
        return numTicketsTotal;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public PerformanceStatus getStatus() {
        return status;
    }

    public void setStatus(PerformanceStatus status) { this.status = status; }

    public Event getEvent() { return event; }

    public Collection<Integer> getReviewRatings() {
        return reviewRatings;
    }

    public Collection<String> getReviewComments() {
        return reviewComments;
    }

    public boolean checkHasNotHappenedYet() {
        return endDateTime.isAfter(LocalDateTime.now());
    }

    public boolean checkCreatedByEP(String email) {
        return event.getOrganiserEmail().equals(email);
    }

    public boolean checkIfEventIsTicketed() {
        return event.getIsTicketed();
    }

    public boolean checkIfTicketsLeft(int numTicketsToBuy) {
        return (numTicketsTotal - numTicketsSold) >= numTicketsToBuy;
    }

    public double getFinalTicketPrice() {
        if (isSponsored) {
            return Math.max(0, ticketPrice - sponsoredAmount);
        }
        return ticketPrice;
    }

    public double getTransactionAmount() { return ticketPrice * numTicketsSold; }

    public void addBooking(Booking b) {
        bookings.add(b);
    }

    public long getID() {
        return performanceId;
    }

    public int getNumTicketsSold() {
        return numTicketsSold;
    }

    public void setNumTicketsSold(int num) {
        this.numTicketsSold = num;
    }

    public String getOrganiserEmail() {
        return event.getOrganiserEmail();
    }

    public String getEventTitle() {
        return event.getTitle();
    }

    public void review(int rating, String comment) {
        reviewRatings.add(rating);
        reviewComments.add(comment);
    }

    public String toString() {
        String info = "Performance ID: " + performanceId
                + "\nEvent: " + event.getTitle()
                + "\nStart: " + startDateTime
                + "\nEnd: " + endDateTime
                + "\nPerformers: " + String.join(", ", performerNames)
                + "\nVenue: " + venueAddress
                + "\nCapacity: " + venueCapacity
                + "\nOutdoors: " + venueIsOutdoors
                + "\nSmoking allowed: " + venueAllowsSmoking;

        if (event.getIsTicketed()) {
            info += "\nTicket Price: £" + getFinalTicketPrice();
            info += "\nTickets Available: " + (numTicketsTotal - numTicketsSold);
        } else {
            info += "\nThis is a non-ticketed event.";
        }

        info += "\nEvent Average Rating: " + event.getAverageRatingOfPerformances();

        Collection<String> reviews = event.getAllPerformanceReviews();
        if (reviews.isEmpty()) {
            info += "\nNo reviews yet.";
        } else {
            info += "\nReviews:";
            for (String r : reviews) {
                info += "\n  - " + r;
            }
        }

        return info;
    }

}
