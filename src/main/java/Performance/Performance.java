package Performance;

import java.time.LocalDateTime;
import java.util.Collection;
import Event.Event;
import Booking.Booking;

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

}
