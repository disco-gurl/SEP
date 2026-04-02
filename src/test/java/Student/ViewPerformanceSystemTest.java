package SystemTests;

import Controller.EventPerformanceController;
import Controller.UserController;
import External.VerificationService;
import User.AdminStaff;
import User.Student;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class ViewPerformanceSystemTest {

    private FakeView display;
    private UserController userController;
    private EventPerformanceController epController;

    static class FakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String lastSuccess;
        private String lastError;
        private String lastPerformanceDisplay;

        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return lastSuccess; }
        public String getLastError() { return lastError; }
        public String getLastPerformanceDisplay() { return lastPerformanceDisplay; }

        @Override
        public String getInput(String inputPrompt) { return inputs.poll(); }

        @Override
        public void displaySuccess(String successMessage) { this.lastSuccess = successMessage; }

        @Override
        public void displayError(String errorMessage) { this.lastError = errorMessage; }

        @Override
        public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {}

        @Override
        public void displaySpecificPerformance(String performanceInfo) {
            this.lastPerformanceDisplay = performanceInfo;
        }

        @Override
        public void displayBookingRecord(String bookingRecord) {}
    }

    static class FakeVerificationService implements VerificationService {
        @Override
        public boolean verifyEntertainmentProvider(String businessRegistrationNumber) {
            return businessRegistrationNumber != null
                    && businessRegistrationNumber.length() == 10;
        }
    }

    private void registerEP(String orgName, String businessNumber, String contactName,
                            String email, String password, String description) {
        display.addInput(orgName);
        display.addInput(businessNumber);
        display.addInput(contactName);
        display.addInput(email);
        display.addInput(password);
        display.addInput(description);
        userController.registerEntertainmentProvider();
    }

    /**
     * Helper that queues email + password and calls login().
     */
    private void login(String email, String password) {
        display.addInput(email);
        display.addInput(password);
        userController.login();
    }

    /**
     * Helper that queues all inputs needed to create a ticketed event
     * with one performance, then calls createEvent().
     * Uses a future date so the performance has not happened yet.
     */
    private void createTicketedEventWithOnePerformance(
            String title, String type, String startDateTime, String endDateTime,
            String performers, String venueAddress, String capacity,
            String outdoors, String smoking, String numTickets, String ticketPrice) {

        display.addInput(title);
        display.addInput(type);
        display.addInput("yes");              // ticketed
        display.addInput(startDateTime);
        display.addInput(endDateTime);
        display.addInput(performers);
        display.addInput(venueAddress);
        display.addInput(capacity);
        display.addInput(outdoors);
        display.addInput(smoking);
        display.addInput(numTickets);
        display.addInput(ticketPrice);
        display.addInput("no");               // don't add another performance
        epController.createEvent();
    }

    /**
     * Helper that queues all inputs needed to create a non-ticketed event
     * with one performance, then calls createEvent().
     */
    private void createNonTicketedEventWithOnePerformance(
            String title, String type, String startDateTime, String endDateTime,
            String performers, String venueAddress, String capacity,
            String outdoors, String smoking) {

        display.addInput(title);
        display.addInput(type);
        display.addInput("no");               // not ticketed
        display.addInput(startDateTime);
        display.addInput(endDateTime);
        display.addInput(performers);
        display.addInput(venueAddress);
        display.addInput(capacity);
        display.addInput(outdoors);
        display.addInput(smoking);
        display.addInput("no");               // don't add another performance
        epController.createEvent();
    }

    @BeforeEach
    void setUp() {
        display = new FakeView();
        userController = new UserController(display, new FakeVerificationService());
        epController = new EventPerformanceController(display);

        // Pre-register a student and an admin (as per requirements, they are pre-registered)
        userController.addUser(new Student("student@ed.ac.uk", "studentpass", "Alice", 123456789));
        userController.addUser(new AdminStaff("admin@ed.ac.uk", "adminpass"));

        // Register an EP and create a ticketed event with one performance
        registerEP("Edinburgh Festivals", "1234567890", "John Smith",
                "john@edinfest.com", "eppass", "Festival organiser");

        login("john@edinfest.com", "eppass");

        createTicketedEventWithOnePerformance(
                "Jazz Night", "Music",
                "2026-12-01 19:00", "2026-12-01 22:00",
                "Miles Davis Tribute Band",
                "Usher Hall, Edinburgh", "500",
                "no", "no",
                "200", "25.00");

        userController.logout();
    }

    @Test
    void displaysPerformanceDetailsAsStudent() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");  // performance ID
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Performance details should be displayed when a student views a valid performance");
        assertTrue(output.contains("Jazz Night"),
                "Displayed performance should include the event name");
        assertTrue(output.contains("Miles Davis Tribute Band"),
                "Displayed performance should include performer names");
        assertTrue(output.contains("Usher Hall, Edinburgh"),
                "Displayed performance should include venue address");
    }

    @Test
    void displaysPerformanceDetailsAsEP() {
        login("john@edinfest.com", "eppass");

        display.addInput("1");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Performance details should be displayed when an EP views a valid performance");
        assertTrue(output.contains("Jazz Night"),
                "Displayed performance should include the event name");
    }

    @Test
    void displaysPerformanceDetailsAsAdmin() {
        login("admin@ed.ac.uk", "adminpass");

        display.addInput("1");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Performance details should be displayed when admin staff views a valid performance");
        assertTrue(output.contains("Jazz Night"),
                "Displayed performance should include the event name");
    }

    @Test
    void displaysTicketInfo() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Performance details should be displayed for a ticketed event");
        assertTrue(output.contains("25.0"),
                "Displayed performance should include ticket price");
        assertTrue(output.contains("200"),
                "Displayed performance should include ticket availability");
    }

    @Test
    void displaysNonTicketedMessage() {
        // Login as EP and create a non-ticketed event
        login("john@edinfest.com", "eppass");

        createNonTicketedEventWithOnePerformance(
                "Free Film Screening", "Movie",
                "2026-12-05 18:00", "2026-12-05 20:00",
                "Director Q&A Panel",
                "George Square Lecture Theatre", "300",
                "no", "no");

        // View the non-ticketed performance (ID should be 2)
        display.addInput("2");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Performance details should be displayed for a non-ticketed event");
        assertTrue(output.contains("non-ticketed"),
                "Non-ticketed event should indicate it is non-ticketed");
    }

    @Test
    void displaysNoReviewsMessage() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Performance details should still display when there are no reviews");
        assertTrue(output.contains("No reviews yet"),
                "Performance with no reviews should display a no-reviews message");
    }

    @Test
    void displaysEventAverageRating() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Performance details should be displayed");
        assertTrue(output.contains("Event Average Rating"),
                "Displayed performance should include the event average rating");
    }

    @Test
    void displaysAllRequiredFields() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output, "Performance details should be displayed");

        // Check all fields that should be present per use case 3.10
        assertTrue(output.contains("Performance ID: 1"),
                "Should display performance ID");
        assertTrue(output.contains("Jazz Night"),
                "Should display event name");
        assertTrue(output.contains("2026-12-01"),
                "Should display performance date");
        assertTrue(output.contains("Miles Davis Tribute Band"),
                "Should display performer names");
        assertTrue(output.contains("Usher Hall, Edinburgh"),
                "Should display venue address");
        assertTrue(output.contains("500"),
                "Should display venue capacity");
    }

    @Test
    void notLoggedIn() {
        // Don't login — currentUser is null after setUp's logout
        display.addInput("1");
        epController.viewPerformance();

        assertEquals("You must be logged in to view a performance", display.getLastError(),
                "Viewing a performance without being logged in should give an error");
    }

    @Test
    void invalidIDFormat() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("abc");  // not a number
        epController.viewPerformance();

        assertEquals("Invalid format", display.getLastError(),
                "Non-numeric performance ID should give an invalid format error");
    }

    @Test
    void nonExistentID() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("999");
        epController.viewPerformance();

        assertEquals("No performance found with performance ID999", display.getLastError(),
                "A performance ID that does not exist should give a not-found error");
    }

    @Test
    void emptyInput() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("");
        epController.viewPerformance();

        assertEquals("Invalid format", display.getLastError(),
                "Empty input for performance ID should give an invalid format error");
    }

    @Test
    void negativeID() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("-1");
        epController.viewPerformance();

        assertEquals("No performance found with performance ID-1", display.getLastError(),
                "A negative performance ID should give a not-found error");
    }

    @Test
    void secondPerformanceOfEventDisplaysRightDetails() {
        // Login as EP and create another event with a different performance
        login("john@edinfest.com", "eppass");

        createTicketedEventWithOnePerformance(
                "Rock Concert", "Music",
                "2026-12-10 20:00", "2026-12-10 23:00",
                "The Rolling Tones",
                "Edinburgh Playhouse", "1000",
                "no", "no",
                "500", "40.00");

        // View the second event's performance (ID 2)
        display.addInput("2");
        epController.viewPerformance();

        String output = display.getLastPerformanceDisplay();
        assertNotNull(output,
                "Second performance should be viewable");
        assertTrue(output.contains("Rock Concert"),
                "Should display the correct event name for the second performance");
        assertTrue(output.contains("The Rolling Tones"),
                "Should display the correct performers for the second performance");
    }
}
