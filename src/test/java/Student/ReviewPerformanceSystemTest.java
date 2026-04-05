package Student;

import Controller.BookingController;
import Controller.UserController;
import External.MockPaymentSystem;
import External.VerificationService;
import Booking.Booking;
import Event.Event;
import Event.EventType;
import Performance.Performance;
import User.AdminStaff;
import User.EntertainmentProvider;
import User.Student;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class ReviewPerformanceSystemTest {

    private FakeView display;
    private UserController userController;
    private BookingController bookingController;
    private Student student;
    private EntertainmentProvider ep;
    private List<Performance> performances;

    static class FakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String lastSuccess;
        private String lastError;

        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return lastSuccess; }
        public String getLastError() { return lastError; }

        @Override
        public String getInput(String inputPrompt) { return inputs.poll(); }

        @Override
        public void displaySuccess(String successMessage) { this.lastSuccess = successMessage; }

        @Override
        public void displayError(String errorMessage) { this.lastError = errorMessage; }

        @Override
        public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {}

        @Override
        public void displaySpecificPerformance(String performanceInfo) {}

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

    private void login(String email, String password) {
        display.addInput(email);
        display.addInput(password);
        userController.login();
    }

    @BeforeEach
    void setUp() {
        display = new FakeView();
        userController = new UserController(display, new FakeVerificationService());
        performances = new ArrayList<>();

        // Pre register users
        student = new Student("student@ed.ac.uk", "studentpass", "Alice", 123456789);
        ep = new EntertainmentProvider("ep@fest.com", "eppass",
                "Festival Org", "1234567890", "John Smith", "Organiser");
        userController.addUser(student);
        userController.addUser(ep);
        userController.addUser(new AdminStaff("admin@ed.ac.uk", "adminpass"));

        // Create a past ticketed event with one performance
        Event pastEvent = new Event(1, "Jazz Night", EventType.Music, true, ep);
        Performance pastPerformance = pastEvent.createPerformance(
                1,
                LocalDateTime.of(2025, 6, 1, 19, 0),   // past start
                LocalDateTime.of(2025, 6, 1, 22, 0),   // past end
                List.of("Miles Davis Tribute Band"),
                "Usher Hall, Edinburgh", 500,
                false, false,
                200, 25.00);
        performances.add(pastPerformance);

        // Create a future ticketed event with one performance
        Event futureEvent = new Event(2, "Rock Concert", EventType.Music, true, ep);
        Performance futurePerformance = futureEvent.createPerformance(
                2,
                LocalDateTime.of(2027, 12, 1, 20, 0),
                LocalDateTime.of(2027, 12, 1, 23, 0),
                List.of("The Rolling Tones"),
                "Edinburgh Playhouse", 1000,
                false, false,
                500, 40.00);
        performances.add(futurePerformance);

        // Manually create a booking for the past performance.
        Booking pastBooking = new Booking(pastPerformance, student, 2);
        student.addBooking(pastBooking);
        pastPerformance.addBooking(pastBooking);

        // Create BookingController with the shared performances list
        bookingController = new BookingController(display, performances, new MockPaymentSystem());
    }


    @Test
    void validRatingAndComment() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("5");
        display.addInput("Absolutely brilliant show!");
        bookingController.reviewPerformance();

        assertEquals("Your review has been submitted successfully.",
                display.getLastSuccess(),
                "A valid review with rating and comment should be submitted successfully");
    }

    @Test
    void validRatingNoComment() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("4");
        display.addInput("");
        bookingController.reviewPerformance();

        assertEquals("Your review has been submitted successfully.",
                display.getLastSuccess(),
                "A review with only a rating (no comment) should be submitted successfully");
    }

    @Test
    void ratingStoredOnPerformance() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("3");
        display.addInput("It was decent");
        bookingController.reviewPerformance();

        Performance reviewed = bookingController.getPerformanceByID(1);
        assertTrue(reviewed.getReviewRatings().contains(3),
                "The rating should be stored on the performance after review");
    }

    @Test
    void commentStoredOnPerformance() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("5");
        display.addInput("Loved every second");
        bookingController.reviewPerformance();

        Performance reviewed = bookingController.getPerformanceByID(1);
        assertTrue(reviewed.getReviewComments().contains("Loved every second"),
                "The comment should be stored on the performance after review");
    }

    //Boundary Tests
    @Test
    void ratingOf1() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("1");
        display.addInput("");
        bookingController.reviewPerformance();

        assertEquals("Your review has been submitted successfully.",
                display.getLastSuccess(),
                "A rating of 1 (minimum) should be accepted");
    }

    @Test
    void ratingOf5() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("5");
        display.addInput("");
        bookingController.reviewPerformance();

        assertEquals("Your review has been submitted successfully.",
                display.getLastSuccess(),
                "A rating of 5 (maximum) should be accepted");
    }

    @Test
    void ratingOf0() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("0");
        bookingController.reviewPerformance();

        assertEquals("Rating must be between 1 and 5.",
                display.getLastError(),
                "A rating of 0 should be rejected as out of range");
    }

    @Test
    void ratingOf6() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("6");
        bookingController.reviewPerformance();

        assertEquals("Rating must be between 1 and 5.",
                display.getLastError(),
                "A rating of 6 should be rejected as out of range");
    }

    @Test
    void negativeRating() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("-1");
        bookingController.reviewPerformance();

        assertEquals("Rating must be between 1 and 5.",
                display.getLastError(),
                "A negative rating should be rejected as out of range");
    }

    @Test
    void nonNumericRating() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("great");
        bookingController.reviewPerformance();

        assertEquals("Invalid rating format.",
                display.getLastError(),
                "A non-numeric rating should be rejected with invalid format error");
    }

    @Test
    void notLoggedIn() {
        // don't login
        bookingController.reviewPerformance();

        assertEquals("You must be logged in to review a performance",
                display.getLastError(),
                "Reviewing without being logged in should give an error");
    }

    @Test
    void loggedInAsEP() {
        login("ep@fest.com", "eppass");

        bookingController.reviewPerformance();

        assertEquals("You must be a student to review a performance.",
                display.getLastError(),
                "An EP should not be able to review a performance");
    }

    @Test
    void loggedInAsAdmin() {
        login("admin@ed.ac.uk", "adminpass");

        bookingController.reviewPerformance();

        assertEquals("You must be a student to review a performance.",
                display.getLastError(),
                "Admin staff should not be able to review a performance");
    }

    @Test
    void futurePerformance() {
        login("student@ed.ac.uk", "studentpass");

        // Performance 2 is in the future
        display.addInput("2");
        bookingController.reviewPerformance();

        assertEquals("You can only review performances that have already taken place.",
                display.getLastError(),
                "Reviewing a performance that has not yet happened should give an error");
    }

    @Test
    void notBookedByStudent() {
        // Create a second student with no bookings
        Student otherStudent = new Student("other@ed.ac.uk", "otherpass", "Bob", 987654321);
        userController.addUser(otherStudent);

        login("other@ed.ac.uk", "otherpass");

        display.addInput("1");
        bookingController.reviewPerformance();

        assertEquals("You can only review performances you have booked.",
                display.getLastError(),
                "A student who did not book the performance should not be able to review it");
    }

    @Test
    void invalidIDFormat() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("abc");
        bookingController.reviewPerformance();

        assertEquals("Invalid peformance ID. ",
                display.getLastError(),
                "A non-numeric performance ID should give an invalid format error");
    }

    @Test
    void nonExistentID() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("999");
        bookingController.reviewPerformance();

        assertEquals("No performance found with 999",
                display.getLastError(),
                "A performance ID that does not exist should give a not-found error");
    }

    @Test
    void afterReview() {
        login("student@ed.ac.uk", "studentpass");

        display.addInput("1");
        display.addInput("4");
        display.addInput("Good show");
        bookingController.reviewPerformance();

        Performance reviewed = bookingController.getPerformanceByID(1);
        double average = reviewed.getEvent().getAverageRatingOfPerformances();

        assertEquals(4.0, average, 0.01,
                "Event average rating should reflect the submitted review");
    }

    @Test
    void multipleReviews() {
        // Create a second student who also booked the past performance
        Student secondStudent = new Student("bob@ed.ac.uk", "bobpass", "Bob", 111222333);
        userController.addUser(secondStudent);
        Performance pastPerf = bookingController.getPerformanceByID(1);
        Booking secondBooking = new Booking(pastPerf, secondStudent, 1);
        secondStudent.addBooking(secondBooking);
        pastPerf.addBooking(secondBooking);

        // First student reviews with rating 4
        login("student@ed.ac.uk", "studentpass");
        display.addInput("1");
        display.addInput("4");
        display.addInput("Great");
        bookingController.reviewPerformance();

        userController.logout();

        // Second student reviews with rating 2
        login("bob@ed.ac.uk", "bobpass");
        display.addInput("1");
        display.addInput("2");
        display.addInput("Not my thing");
        bookingController.reviewPerformance();

        double average = pastPerf.getEvent().getAverageRatingOfPerformances();
        assertEquals(3.0, average, 0.01,
                "Event average should be (4+2)/2 = 3.0 after two reviews");
    }
}
