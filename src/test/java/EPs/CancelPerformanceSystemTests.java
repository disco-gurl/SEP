package EPs;

import Booking.Booking;
import Controller.EventPerformanceController;
import Controller.UserController;
import Event.Event;
import External.MockVerificationService;
import Performance.Performance;
import Performance.PerformanceStatus;
import User.AdminStaff;
import User.EntertainmentProvider;
import User.Student;
import View.View;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CancelPerformanceSystemTests {

    private View mockView;
    private UserController userController;
    private EventPerformanceController eventPerformanceController;

    private Student student;
    private EntertainmentProvider ep;
    private EntertainmentProvider anotherEP;
    private AdminStaff admin;

    @BeforeEach
    void setUp() {
        mockView = mock(View.class);
        userController = new UserController(mockView, new MockVerificationService());
        eventPerformanceController = new EventPerformanceController(mockView);

        userController.logout();

        student   = new Student("student@uni.ac.uk", "password123", "Alice Smith",
                123456789);
        ep        = new EntertainmentProvider(
                "ep@shows.com", "pass456", "Cool Shows Ltd",
                "1234567890", "Bob Provider", "Great shows");
        anotherEP = new EntertainmentProvider(
                "other@shows.com", "otherpass", "Other Shows Ltd",
                "0987654321", "Carol Other", "Other shows");
        admin     = new AdminStaff("admin@uni.ac.uk", "adminpass");

        userController.addUser(student);
        userController.addUser(ep);
        userController.addUser(anotherEP);
        userController.addUser(admin);
    }

    // helpers

    private void loginAs(String email, String password) {
        when(mockView.getInput("Enter email: ")).thenReturn(email);
        when(mockView.getInput("Enter password: ")).thenReturn(password);
        userController.login();
    }

    // log in as the primary EP and create one ticketed Music event with one future performance then log out
    private void createEventPrimaryEP() {
        loginAs("ep@shows.com", "pass456");

        when(mockView.getInput("Enter event title: ")).thenReturn("The Fringe");
        when(mockView.getInput("Enter event type (Music, Theatre, Dance, Movie, Sports): "))
                .thenReturn("Music");
        when(mockView.getInput("Is the event ticketed? ")).thenReturn("yes");
        when(mockView.getInput("Enter performance start date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-09-10 19:00");
        when(mockView.getInput("Enter performance end date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-09-10 21:00");
        when(mockView.getInput("Enter performer names (comma-separated): "))
                .thenReturn("The Edinburgers");
        when(mockView.getInput("Enter venue address: ")).thenReturn("Meadows");
        when(mockView.getInput("Enter venue capacity: ")).thenReturn("2000");
        when(mockView.getInput("Is the venue outdoors? ")).thenReturn("yes");
        when(mockView.getInput("Does the venue allow smoking? ")).thenReturn("no");
        when(mockView.getInput("Enter total number of tickets: ")).thenReturn("500");
        when(mockView.getInput("Enter ticket price: ")).thenReturn("30.00");
        when(mockView.getInput("Would you like to add another performance? ")).thenReturn("no");

        eventPerformanceController.createEvent();
        userController.logout();
    }

    // log in as the second EP and cretae one non-tickted Theatre event with one performance then log out
    private void createEventDifferentEP() {
        loginAs("other@shows.com", "otherpass");

        when(mockView.getInput("Enter event title: ")).thenReturn("Edinburgh Theatre");
        when(mockView.getInput("Enter event type (Music, Theatre, Dance, Movie, Sports): "))
                .thenReturn("Theatre");
        when(mockView.getInput("Is the event ticketed? ")).thenReturn("no");
        when(mockView.getInput("Enter performance start date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-09-11 14:00");
        when(mockView.getInput("Enter performance end date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-09-11 16:00");
        when(mockView.getInput("Enter performer names (comma-separated): "))
                .thenReturn("Theatrists");
        when(mockView.getInput("Enter venue address: ")).thenReturn("City Theatre");
        when(mockView.getInput("Enter venue capacity: ")).thenReturn("300");
        when(mockView.getInput("Is the venue outdoors? ")).thenReturn("no");
        when(mockView.getInput("Does the venue allow smoking? ")).thenReturn("no");
        when(mockView.getInput("Would you like to add another performance? ")).thenReturn("no");

        eventPerformanceController.createEvent();
        userController.logout();
    }

    // tests

    @Test
    void testGuestRejected() {
        eventPerformanceController.cancelPerformance();

        verify(mockView).displayError("You must be logged in as an Entertainment Provider.");
    }

    @Test
    void testStudentRejected() {
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        eventPerformanceController.cancelPerformance();

        verify(mockView).displayError("You must be logged in as an Entertainment Provider.");
    }

    @Test
    void testAdminRejected() {
        loginAs("admin@uni.ac.uk", "adminpass");
        clearInvocations(mockView);

        eventPerformanceController.cancelPerformance();

        verify(mockView).displayError("You must be logged in as an Entertainment Provider.");
    }

    @Test
    void testInvalidIDRejected() {
        createEventPrimaryEP();
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter performance ID to cancel: "))
                .thenReturn("9999")   // does not exist
                .thenReturn("1");     // exists, owned by this EP
        when(mockView.getInput("Enter a message to send to students: "))
                .thenReturn("Show cancelled.");

        eventPerformanceController.cancelPerformance();

        verify(mockView).displayError("Performance ID invalid or does not belong to you.");
        verify(mockView).displaySuccess("Performance 1 has been cancelled successfully.");
    }

    @Test
    void testNotOwnedByEPRejected() {
        createEventPrimaryEP();    // ep owns performance ID 1
        createEventDifferentEP();    // anotherEP owns performance ID 2
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter performance ID to cancel: "))
                .thenReturn("2")   // belongs to anotherEP
                .thenReturn("1");  // belongs to primary ep
        when(mockView.getInput("Enter a message to send to students: "))
                .thenReturn("Cancellation notice.");

        eventPerformanceController.cancelPerformance();

        verify(mockView).displayError("Performance ID invalid or does not belong to you.");
    }

    @Test
    void testEmptyMessageRejected() {
        createEventPrimaryEP();
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter performance ID to cancel: ")).thenReturn("1");
        when(mockView.getInput("Enter a message to send to students: "))
                .thenReturn("")
                .thenReturn("Sorry, show is cancelled.");

        eventPerformanceController.cancelPerformance();

        verify(mockView).displayError("Message cannot be empty.");
    }

    @Test
    void testWhitespaceMessageRejected() {
        createEventPrimaryEP();
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter performance ID to cancel: ")).thenReturn("1");
        when(mockView.getInput("Enter a message to send to students: "))
                .thenReturn("   ")
                .thenReturn("Valid message.");

        eventPerformanceController.cancelPerformance();

        verify(mockView).displayError("Message cannot be empty.");
    }

    @Test
    void testPerformanceNoBookingsCancellation() {
        createEventPrimaryEP();
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter performance ID to cancel: ")).thenReturn("1");
        when(mockView.getInput("Enter a message to send to students: "))
                .thenReturn("The show has been cancelled.");

        eventPerformanceController.cancelPerformance();

        verify(mockView).displaySuccess("Performance 1 has been cancelled successfully.");
        verify(mockView, never()).displayError(anyString());
    }

    @Test
    void testPerformanceWithBookingsRefund() {
        createEventPrimaryEP();

        Event epEvent = ep.getEvents().iterator().next();
        Performance perf = epEvent.getPerformances().iterator().next();

        Booking booking = new Booking(perf, student, 2);
        perf.addBooking(booking);
        perf.setNumTicketsSold(2);
        student.addBooking(booking);

        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter performance ID to cancel: ")).thenReturn("1");
        when(mockView.getInput("Enter a message to send to students: "))
                .thenReturn("Unfortunately the event must be cancelled.");

        eventPerformanceController.cancelPerformance();

        // MockPaymentSystem will return true for a valid refund call.
        verify(mockView).displaySuccess("Performance 1 has been cancelled successfully.");
        verify(mockView, never()).displayError(anyString());
    }

    @Test
    void testSetStatusCancelled() {
        createEventPrimaryEP();

        Event epEvent = ep.getEvents().iterator().next();
        Performance perf = epEvent.getPerformances().iterator().next();

        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter performance ID to cancel: ")).thenReturn("1");
        when(mockView.getInput("Enter a message to send to students: "))
                .thenReturn("Cancellation notice.");

        eventPerformanceController.cancelPerformance();

        assertEquals(PerformanceStatus.CANCELLED, perf.getStatus(),
                "Performance status should be CANCELLED after successful cancellation");
    }

}
