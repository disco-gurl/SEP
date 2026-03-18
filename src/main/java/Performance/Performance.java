package Performance;

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
    private double sponseredAmount;
    private Collection<int> reviewRatings;
    private Collection<String> reviewComments;
    private PerformanceStatus status;
}
