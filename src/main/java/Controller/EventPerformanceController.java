package Controller;

import External.MockPaymentSystem;
import External.PaymentSystem;
import User.EntertainmentProvider;
import User.Student;
import User.StudentPreferences;
import View.View;
import Event.Event;
import Performance.Performance;
import Event.EventType;

import User.AdminStaff;
import User.EntertainmentProvider;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static Performance.PerformanceStatus.CANCELLED;

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

    // finds event by title, used in create event to check for duplicates
    private Event getEventByTitle(String title) {

        for (Event e : events) {
            if (e.getTitle().equalsIgnoreCase(title)) {
                return e;
            }
        }
        return null;
    }

    // helper to check if sponsorship is valid based on the sequence diagram
    private boolean checkIfSponsorshipPossible(Performance performance, double amount) {

        // check that its ticketed first
        boolean isTicketed = performance.checkIfEventIsTicketed();
        if (!isTicketed) {
            getView().displayError("The requested performance's event is non ticketed. It cannot be sponsored.");
            return false;
        }

        // amount has to be positive and not more than the ticket price
        double ticketPrice = performance.getTicketPrice();

        if (amount < 0 || amount > ticketPrice) {
            getView().displayError("The amount provided is invalid.");
            return false;
        }

        return true;
    }


    //Create event use case. Only accessible to EP's. Gets the event details then lets them add performances in a loop.
    public Event createEvent() {

        if (getCurrentUser() == null) {
            getView().displayError("You must be logged in to create an event.");
            return null;
        }

        if (!(getCurrentUser() instanceof EntertainmentProvider)) {
            getView().displayError("Only Entertainment Providers can create events.");
            return null;
        }

        EntertainmentProvider ep = (EntertainmentProvider) getCurrentUser();

        // get event info from the user
        String title = getView().getInput("Enter event title: ");
        if (title == null || title.trim().isEmpty()) {
            getView().displayError("Event title cannot be empty.");
            return null;
        }

        // make sure no duplicate titles
        if (getEventByTitle(title) != null) {
            getView().displayError("An event with this title already exists.");
            return null;
        }

        String typeInput = getView().getInput("Enter event type (Music, Theatre, Dance, Movie, Sports): ");
        EventType type;
        try {
            type = EventType.valueOf(typeInput.trim());
        } catch (IllegalArgumentException e) {
            getView().displayError("Invalid event type. Must be one of: Music, Theatre, Dance, Movie, Sports.");
            return null;
        }

        String ticketedInput = getView().getInput("Is the event ticketed? (yes/no): ");
        boolean isTicketed;
        if (ticketedInput.trim().equalsIgnoreCase("yes")) {
            isTicketed = true;
        } else if (ticketedInput.trim().equalsIgnoreCase("no")) {
            isTicketed = false;
        } else {
            getView().displayError("Invalid input. Please enter 'yes' or 'no'.");
            return null;
        }

        Event event = new Event(nextEventID++, title.trim(), type, isTicketed, ep);

        // now let them add performances in a loop
        boolean adding = true;
        while (adding) {
            getView().displaySuccess("Adding a performance to event: " + title);

            // get the start and end times
            String startInput = getView().getInput("Enter performance start date and time (yyyy-MM-dd HH:mm): ");
            LocalDateTime startDateTime;
            try {
                startDateTime = LocalDateTime.parse(startInput.trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                getView().displayError("Invalid date/time format. Please use yyyy-MM-dd HH:mm.");
                continue;
            }

            String endInput = getView().getInput("Enter performance end date and time (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDateTime;
            try {
                endDateTime = LocalDateTime.parse(endInput.trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                getView().displayError("Invalid date/time format. Please use yyyy-MM-dd HH:mm.");
                continue;
            }

            if (!endDateTime.isAfter(startDateTime)) {
                getView().displayError("End time must be after start time.");
                continue;
            }

            // check theres no overlap with existing performances for this event
            if (event.hasPerformanceAtSameTimes(startDateTime, endDateTime)) {
                getView().displayError("There is already a performance at the same time for this event.");
                continue;
            }

            String performersInput = getView().getInput("Enter performer names (comma-separated): ");
            if (performersInput == null || performersInput.trim().isEmpty()) {
                getView().displayError("Performer names cannot be empty.");
                continue;
            }
            // split by comma and put into a list
            Collection<String> performerNames = new ArrayList<>(
                    Arrays.asList(performersInput.trim().split("\\s*,\\s*")));

            String venueAddress = getView().getInput("Enter venue address: ");
            if (venueAddress == null || venueAddress.trim().isEmpty()) {
                getView().displayError("Venue address cannot be empty.");
                continue;
            }

            String capacityInput = getView().getInput("Enter venue capacity: ");

            int venueCapacity;
            try {
                venueCapacity = Integer.parseInt(capacityInput.trim());
                if (venueCapacity <= 0) {
                    getView().displayError("Venue capacity must be a positive number.");
                    continue;
                }
            } catch (NumberFormatException e) {
                getView().displayError("Invalid venue capacity format.");
                continue;
            }

            String outdoorsInput = getView().getInput("Is the venue outdoors? (yes/no): ");
            boolean venueIsOutdoors = outdoorsInput.trim().equalsIgnoreCase("yes");

            String smokingInput = getView().getInput("Does the venue allow smoking? (yes/no): ");
            boolean venueAllowsSmoking = smokingInput.trim().equalsIgnoreCase("yes");

            // only ask for ticket info if the event is ticketed
            int numTickets = 0;
            double ticketPrice = 0;

            if (isTicketed) {

                String ticketsInput = getView().getInput("Enter total number of tickets: ");
                try {
                    numTickets = Integer.parseInt(ticketsInput.trim());
                    if (numTickets <= 0) {
                        getView().displayError("Number of tickets must be a positive number.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    getView().displayError("Invalid number of tickets format.");
                    continue;
                }

                String priceInput = getView().getInput("Enter ticket price: ");
                try {

                    ticketPrice = Double.parseDouble(priceInput.trim());
                    if (ticketPrice < 0) {

                        getView().displayError("Ticket price cannot be negative.");
                        continue;
                    }
                } catch (NumberFormatException e) {

                    getView().displayError("Invalid ticket price format.");
                    continue;
                }
            }

            // use the event's createPerformance method from the class diagram
            Performance performance = event.createPerformance(

                    nextPerformanceID++, startDateTime, endDateTime,
                    performerNames, venueAddress.trim(), venueCapacity,
                    venueIsOutdoors, venueAllowsSmoking, numTickets, ticketPrice);

            performances.add(performance);

            getView().displaySuccess("Performance added successfully (ID: " + performance.getID() + ").");

            String moreInput = getView().getInput("Add another performance? (yes/no): ");
            if (!moreInput.trim().equalsIgnoreCase("yes")) {
                adding = false;
            }
        }

        // need at least one performance
        if (event.getPerformances().isEmpty()) {

            getView().displayError("Event must have at least one performance. Event creation cancelled.");
            nextEventID--;
            return null;
        }

        events.add(event);
        ep.addEvent(event);

        getView().displaySuccess("Event '" + title + "' created successfully with "
                + event.getPerformances().size() + " performance(s).");

        return event;
    }


    //Sponsor performance use case. Admin enters performance ID and amount,
    //system validates it and applies the sponsorship.
    public void sponsorPerformance() {
        if (getCurrentUser() == null) {
            getView().displayError("You must be logged in to sponsor a performance.");
            return;
        }

        // only admin can sponsor
        if (!(getCurrentUser() instanceof AdminStaff)) {
            getView().displayError("Only admin staff can sponsor performances.");
            return;
        }

        Performance performance = null;
        boolean possible = false;

        // keep looping until they give valid input (based on sequence diagram)
        while (performance == null || !possible) {

            String perfInput = getView().getInput("Enter the performance ID to sponsor: ");
            long performanceID;
            try {
                performanceID = Long.parseLong(perfInput.trim());
            } catch (NumberFormatException e) {
                getView().displayError("Invalid performance ID format.");
                continue;
            }

            performance = getPerformanceByID(performanceID);
            if (performance == null) {
                getView().displayError("Performance with given number does not exist.");
                continue;
            }

            String amountInput = getView().getInput("Enter sponsorship amount: ");
            double amount;
            try {
                amount = Double.parseDouble(amountInput.trim());
            } catch (NumberFormatException e) {
                getView().displayError("Invalid amount format.");
                performance = null;
                continue;
            }

            possible = checkIfSponsorshipPossible(performance, amount);
            if (!possible) {
                performance = null;
                continue;
            }

            // actually apply it
            performance.sponsor(amount);
            getView().displaySuccess("Sponsorship Successful!");
        }
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

        getView().displaySpecificPerformance(performance.toString());
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
            String input = getView().getInput("Enter a date to search for performances (yyyy-mm-dd): ");
            try {
                targetDate = LocalDateTime.parse(input.trim() + "T00:00:00"); // assuming date-only input
                break; // valid date, exit the loop
            } catch (Exception e) {
                getView().displayError("Invalid date format. Please use yyyy-mm-dd");
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

                StudentPreferences prefs = student.getStudentPreferences();

                for (Performance p : performancesOnDate) {
                    EventType type  = p.getEvent().getType();
                    boolean matches = false;

                    if (prefs.getPreferDanceEvents() && type == EventType.Dance) {
                        matches = true;
                    }
                    if (prefs.getPreferTheaterEvents() && type == EventType.Theatre) {
                        matches = true;
                    }
                    if (prefs.getPreferMovieEvents() && type == EventType.Movie) {
                        matches = true;
                    }
                    if (prefs.getPreferMusicEvents() && type == EventType.Music) {
                        matches = true;
                    }
                    if (prefs.getPreferSportsEvents() && type == EventType.Sports) {
                        matches = true;
                    }


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

    public void cancelPerformance() {
        if (!(getCurrentUser() instanceof EntertainmentProvider ep)) {
            getView().displayError("You must be logged in as an Entertainment Provider.");
            return;
        }

        Performance performance = null;
        while (performance == null) {
            String input = getView().getInput("Enter performance ID to cancel: ");
            long performanceID;
            try {
                performanceID = Long.parseLong(input.trim());
            } catch (NumberFormatException e) {
                getView().displayError("Invalid performance ID format.");
                continue;
            }

            performance = getPerformanceByID(performanceID);

            if (performance == null || !performance.checkCreatedByEP(ep.getEmail())) {
                getView().displayError("Performance ID invalid or does not belong to you.");
                performance = null;
            }
        }

        String message = "";
        while (message.isBlank()) {
            message = getView().getInput("Enter a message to send to students: ").trim();
            if (message.isBlank()) {
                getView().displayError("Message cannot be empty.");
            }
        }

        if (!performance.getBookings().isEmpty()) {

            MockPaymentSystem paymentSystem = new MockPaymentSystem();

            boolean refundSuccess = paymentSystem.processRefund(
                    performance.getNumTicketsSold(),
                    performance.getEventTitle(),
                    " ",
                    0,
                    performance.getOrganiserEmail(),
                    performance.getTransactionAmount(),
                    message);

            if (!refundSuccess) {
                getView().displayError("Refunds could not be processed. Performance not cancelled.");
                return;
            }
        }

        performance.setStatus(CANCELLED);
        getView().displaySuccess("Performance " + performance.getID() +
                " has been cancelled successfully.");
    }
}
