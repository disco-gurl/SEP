package EPs;

import Controller.EventPerformanceController;
import Controller.UserController;
import External.VerificationService;
import Event.Event;
import User.AdminStaff;
import User.EntertainmentProvider;
import User.Student;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class CreateEventSystemTest {

    private fakeView display;
    private UserController userController;
    private EventPerformanceController epController;

    static class fakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String Success;
        private String Error;

        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return Success; }
        public String getLastError() { return Error; }

        @Override
        public String getInput(String inputPrompt) { return inputs.poll(); }

        @Override
        public void displaySuccess(String successMessage) { this.Success = successMessage; }

        @Override
        public void displayError(String errorMessage) { this.Error = errorMessage; }

        @Override
        public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {}

        @Override
        public void displaySpecificPerformance(String performanceInfo) {}

        @Override
        public void displayBookingRecord(String bookingRecord) {}
    }

    static class fakeVerificationService implements VerificationService {
        @Override
        public boolean verifyEntertainmentProvider(String businessRegistrationNumber) {
            return businessRegistrationNumber != null
                    && businessRegistrationNumber.length() == 10;
        }
    }

    @BeforeEach
    void setUp() {
        display = new fakeView();
        userController = new UserController(display, new fakeVerificationService());
        epController = new EventPerformanceController(display);

        userController.addUser(new Student("student@ed.ac.uk", "studentpass", "Alice",
                123456789));
        userController.addUser(new AdminStaff("admin@ed.ac.uk", "adminpass"));

        //register an EP
        display.addInput("Edinburgh Festivals");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("eppass");
        display.addInput("Festival organiser");
        userController.registerEntertainmentProvider();

        display.addInput("john@edinfest.com");
        display.addInput("eppass");
        userController.login();
    }


    @Test
    void successfulTicketedEvent() {
        display.addInput("Jazz Night");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A, Band B");
        display.addInput("Usher Hall, Edinburgh");
        display.addInput("500");
        display.addInput("no");
        display.addInput("no");
        display.addInput("200");
        display.addInput("25.00");
        display.addInput("no");   //no more performances

        Event event = epController.createEvent();

        assertNotNull(event, "Event should be created");
        assertTrue(display.getLastSuccess().contains("Jazz Night"),
                "should mention the event title");
    }

    @Test
    void successfulNonticketed() {
        display.addInput("Free Film Night");
        display.addInput("Movie");
        display.addInput("no");
        display.addInput("2026-12-05 18:00");
        display.addInput("2026-12-05 20:00");
        display.addInput("Director Panel");
        display.addInput("George Square Theatre");
        display.addInput("300");
        display.addInput("no");
        display.addInput("no");
        display.addInput("no");
        Event event = epController.createEvent();
        assertNotNull(event, "non ticketed event should work");
        assertFalse(event.getIsTicketed(), "should not be ticketed");
    }

    @Test
    void multiplePerformances() {
        display.addInput("Summer Festival");
        display.addInput("Music");
        display.addInput("yes");

        //first one
        display.addInput("2026-08-01 14:00");
        display.addInput("2026-08-01 17:00");
        display.addInput("Artist A");
        display.addInput("Meadows Park");
        display.addInput("1000");
        display.addInput("yes");
        display.addInput("no");
        display.addInput("500");
        display.addInput("15.00");
        display.addInput("yes");   //add another

        //second one different day so no overlap
        display.addInput("2026-08-02 14:00");
        display.addInput("2026-08-02 17:00");
        display.addInput("Artist B");
        display.addInput("Meadows Park");
        display.addInput("1000");
        display.addInput("yes");
        display.addInput("no");
        display.addInput("500");
        display.addInput("15.00");
        display.addInput("no");

        Event event = epController.createEvent();
        assertNotNull(event, "event with 2 performances should be created");
        assertEquals(2, event.getPerformances().size(), "should have 2 performances");
    }

    @Test
    void notLoggedIn() {
        userController.logout();
        Event event = epController.createEvent();

        assertNull(event, "cant create event without logging in");
        assertEquals("You must be logged in to create an event.", display.getLastError());
    }

    @Test
    void studentCantCreate() {
        userController.logout();
        display.addInput("student@ed.ac.uk");
        display.addInput("studentpass");
        userController.login();

        Event event = epController.createEvent();
        assertNull(event, "students shouldnt create events");
        assertEquals("Only Entertainment Providers can create events.", display.getLastError());
    }

    @Test
    void adminCantCreate() {
        userController.logout();
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        Event event = epController.createEvent();
        assertNull(event, "admin cant create events");
        assertEquals("Only Entertainment Providers can create events.", display.getLastError());
    }

    @Test
    void emptyTitle() {
        display.addInput("");
        Event event = epController.createEvent();
        assertNull(event);
        assertEquals("Event title cannot be empty.", display.getLastError());
    }

    @Test
    void nullTitle() {
        display.addInput(null);
        Event event = epController.createEvent();

        assertNull(event);
        assertEquals("Event title cannot be empty.", display.getLastError());
    }

    @Test
    void duplicateTitle() {
        //make one event first
        display.addInput("Jazz Night");
        display.addInput("Music");
        display.addInput("no");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band");
        display.addInput("Usher Hall");
        display.addInput("500");
        display.addInput("no");
        display.addInput("no");
        display.addInput("no");
        epController.createEvent();

        //try same title again
        display.addInput("Jazz Night");
        Event event = epController.createEvent();
        assertNull(event, "duplicate title should fail");
        assertEquals("An event with this title already exists.", display.getLastError());
    }

    @Test
    void duplicateTitleDifferentCase() {
        display.addInput("Jazz Night");
        display.addInput("Music");
        display.addInput("no");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band");
        display.addInput("Usher Hall");
        display.addInput("500");
        display.addInput("no");
        display.addInput("no");
        display.addInput("no");
        epController.createEvent();

        display.addInput("JAZZ NIGHT"); //same but uppercase

        Event event = epController.createEvent();

        assertNull(event, "case insensitive check should catch this");
        assertEquals("An event with this title already exists.", display.getLastError());
    }

    @Test
    void badEventType() {
        display.addInput("Rock Show");
        display.addInput("Rock");
        Event event = epController.createEvent();
        assertNull(event);
        assertEquals("Invalid event type. Must be one of: Music, Theatre, Dance, Movie, Sports.",
                display.getLastError());
    }

    @Test
    void invalidTicketedAnswer() {
        display.addInput("Test Event");
        display.addInput("Music");
        display.addInput("maybe");

        Event event = epController.createEvent();

        assertNull(event);
        assertEquals("Invalid input. Please enter 'yes' or 'no'.", display.getLastError());
    }

    @Test
    void badStartDate() {
        display.addInput("Test Event");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("not-a-date");
        //should loop and let us retry
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("10.00");
        display.addInput("no");
        Event event = epController.createEvent();
        assertEquals("Invalid date/time format. Please use yyyy-MM-dd HH:mm.",
                display.getLastError());
        assertNotNull(event, "should still work after fixing date");
    }

    @Test
    void endBeforeStart() {
        display.addInput("Test Event");
        display.addInput("Music");
        display.addInput("yes");

        //end before start
        display.addInput("2026-12-01 22:00");
        display.addInput("2026-12-01 19:00");

        //fix it
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("10.00");
        display.addInput("no");

        Event event = epController.createEvent();

        assertEquals("End time must be after start time.", display.getLastError());
        assertNotNull(event, "should create after fixing times");
    }

    @Test
    void overlappingTimes() {
        display.addInput("Test Event");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("10.00");
        display.addInput("yes"); //add another
        //this overlaps with first
        display.addInput("2026-12-01 20:00");
        display.addInput("2026-12-01 23:00");
        //after error give non overlapping
        display.addInput("2026-12-02 19:00");
        display.addInput("2026-12-02 22:00");
        display.addInput("Band B");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("10.00");
        display.addInput("no");
        Event event = epController.createEvent();

        assertEquals("There is already a performance at the same time for this event.",
                display.getLastError());
        assertNotNull(event);
    }

    @Test
    void emptyPerformers() {
        display.addInput("Test Event");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("");
        //retry
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("10.00");
        display.addInput("no");

        epController.createEvent();
        assertEquals("Performer names cannot be empty.", display.getLastError());
    }

    @Test
    void zeroCapacity() {
        display.addInput("Test Event");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("0");
        //retry
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("10.00");
        display.addInput("no");
        epController.createEvent();

        assertEquals("Venue capacity must be a positive number.", display.getLastError());
    }

    @Test
    void negativePrice() {
        display.addInput("Test Event");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("-5.00");
        //retry
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("50");
        display.addInput("10.00");
        display.addInput("no");
        epController.createEvent();
        assertEquals("Ticket price cannot be negative.", display.getLastError());
    }

    @Test
    void freeTicketedEvent() {
        //price 0 should be allowed even if ticketed
        display.addInput("Free Ticketed Show");
        display.addInput("Dance");
        display.addInput("yes");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Dancers");
        display.addInput("Studio");
        display.addInput("50");
        display.addInput("no");
        display.addInput("no");
        display.addInput("30");
        display.addInput("0.00");
        display.addInput("no");
        Event event = epController.createEvent();

        assertNotNull(event, "0 price should be fine");
        assertTrue(event.getIsTicketed(), "should still be ticketed");
    }

    @Test
    void outdoorsSmokingVenue() {
        display.addInput("Outdoor Gig");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("2026-07-15 15:00");
        display.addInput("2026-07-15 20:00");
        display.addInput("DJ Set");
        display.addInput("Meadows Park");
        display.addInput("2000");
        display.addInput("yes"); //outdoors
        display.addInput("yes"); //smoking
        display.addInput("1000");
        display.addInput("5.00");
        display.addInput("no");

        Event event = epController.createEvent();
        assertNotNull(event, "outdoor smoking venue should be fine");
    }
}
