package Student;

import Booking.Booking;
import Controller.BookingController;
import Controller.UserController;
import Event.Event;
import Event.EventType;
import External.MockPaymentSystem;
import External.MockVerificationService;
import Performance.Performance;
import User.AdminStaff;
import User.EntertainmentProvider;
import User.Student;
import View.View;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class CancelBookingSystemTests {

    private View mockView;
    private UserController userController;
    private BookingController bookingController;
    private Collection<Performance> sharedPerformances;

    private Student student;
    private EntertainmentProvider ep;
    private AdminStaff admin;

    private Performance futurePerformance;

    private Performance imminentPerformance;

    @BeforeEach
    void setUp() {
        mockView = mock(View.class);
        sharedPerformances = new ArrayList<>();

        userController = new UserController(mockView, new MockVerificationService());
        bookingController = new BookingController(mockView, sharedPerformances, new MockPaymentSystem());

        userController.logout();

        student = new Student("student@uni.ac.uk", "password123", "Libby Goode",
                123456789);
        ep = new EntertainmentProvider(
                "ep@shows.com", "pass456", "Edinburgh Concert Company",
                "1234567890", "Jane Goodall", "Concerts in Edinburgh");
        admin = new AdminStaff("admin@uni.ac.uk", "adminpass");

        userController.addUser(student);
        userController.addUser(ep);
        userController.addUser(admin);

        futurePerformance = buildPerformance(
                1L, LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(7).plusHours(2));
        imminentPerformance = buildPerformance(
                2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3));

        sharedPerformances.add(futurePerformance);
        sharedPerformances.add(imminentPerformance);
    }

    // helpers

    private void loginAs(String email, String password) {
        when(mockView.getInput("Enter email: ")).thenReturn(email);
        when(mockView.getInput("Enter password: ")).thenReturn(password);
        userController.login();
    }

    private Performance buildPerformance(long perfId, LocalDateTime start, LocalDateTime end) {
        Event event = new Event(perfId * 100, "Show " + perfId, EventType.Music, true, ep);
        return event.createPerformance(perfId, start, end, List.of("Artist"),
                "Murrayfield", 500, false, false, 100, 20.0);
    }

    private Booking createBookingWithId(Performance performance, long bookingNumber)
            throws Exception {
        Booking booking = new Booking(performance, student, 2);
        Field field = Booking.class.getDeclaredField("bookingNumber");
        field.setAccessible(true);
        field.set(booking, bookingNumber);
        student.addBooking(booking);
        performance.addBooking(booking);
        performance.setNumTicketsSold(2);
        return booking;
    }

    // tests

    @Test
    void testGuestRejected() {
        bookingController.cancelBooking(1);

        verify(mockView).displayError("You must be logged in to cancel a booking.");
    }
    @Test
    void testEPRejected() {
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        bookingController.cancelBooking(1);

        verify(mockView).displayError("You must be a student to cancel a booking.");
    }
    @Test
    void testAdminRejected() {
        loginAs("admin@uni.ac.uk", "adminpass");
        clearInvocations(mockView);

        bookingController.cancelBooking(1);

        verify(mockView).displayError("You must be a student to cancel a booking.");
    }
    @Test
    void testUnknownIDNotFound() {
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        // Student has no bookings at all.
        bookingController.cancelBooking(99999);

        verify(mockView).displayError("Booking not found.");
    }
    @Test
    void testWithinTwentyFourHoursRejected() throws Exception {
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        createBookingWithId(imminentPerformance, 42L);

        bookingController.cancelBooking(42);

        verify(mockView).displayError("Cannot cancel booking within 24 hours.");
    }
    @Test
    void testCancellationRefundSuccess() throws Exception {
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        createBookingWithId(futurePerformance, 1L);

        bookingController.cancelBooking(1);

        verify(mockView).displaySuccess("Booking has been cancelled.");
        verify(mockView, never()).displayError(anyString());
    }

}
