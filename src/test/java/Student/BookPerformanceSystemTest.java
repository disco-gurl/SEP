package Student;

import Controller.BookingController;
import Event.*;
import External.PaymentSystem;
import Performance.Performance;
import User.EntertainmentProvider;
import User.Student;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BookPerformanceSystemTest {
    private EntertainmentProvider fakeEP;
    private Event fakeEvent;
    private Performance fakePerformance;
    private fakeView display;
    private Collection<Performance> performances;
    private BookingController controller;
    private Student student;

    static class fakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String Success;
        private String Error;


        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return Success; }
        public String getLastError() { return Error; }

        @Override
        public String getInput(String inputPrompt) {
            return inputs.poll();
        }

        @Override
        public void displaySuccess(String successMessage) { this.Success = successMessage; }

        @Override
        public void displayError(String errorMessage) { this.Error = errorMessage; }

        @Override
        public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {

        }

        @Override
        public void displaySpecificPerformance(String performanceInfo) {

        }

        @Override
        public void displayBookingRecord(String bookingRecord) { //another error

        }

    }

    static class fakePayment implements PaymentSystem {
        private final boolean paymentSucceeds;

        fakePayment(boolean paymentSucceeds) {
            this.paymentSucceeds = paymentSucceeds;
        }

        @Override
        public boolean processPayment(int numTickets, String eventTitle, String studentEmail,
                                      int studentPhone, String epEmail, double transactionAmount) {
            return paymentSucceeds;
        }

        @Override
        public boolean processRefund(int numTickets, String eventTitle, String studentEmail,
                                     int studentPhone, String epEmail, double transactionAmount,
                                     String organiserMsg) {
            return paymentSucceeds;
        }
    }

    @BeforeEach
    void setUp() {
        display = new fakeView();
        fakeEP = new EntertainmentProvider("club@club.uk", "havefun",
                "CLUB123", "123errt", "123clubs",
                "place to have fun");

        fakeEvent = new Event(125438060608L, "Easter", EventType.Dance, true, fakeEP);

        Collection<String> names = new ArrayList<>();
        names.add("DJ");

        fakePerformance = fakeEvent.createPerformance(
                123L,
                LocalDateTime.of(2005, 6, 8, 18, 0),
                LocalDateTime.of(2005, 7, 12, 4, 0),
                names, "1 george street", 200,
                false, false, 50, 15.00);

        performances = new ArrayList<>();
        performances.add(fakePerformance);

        student = new Student("tracey@ed.ac.uk", "group29", "Tracey", 77);

        controller = new BookingController(display, performances, new fakePayment(true));
        controller.setCurrentUser(student);
    }

    @Test
    void successfulBookPerformance() {
        display.addInput("123");
        display.addInput("6");

        controller.bookPerformance();

        assertEquals("Booking successful!", display.getLastSuccess());
    }

    @Test
    void buyExactTicket() {
        display.addInput("123");
        display.addInput("50");

        controller.bookPerformance();

        assertEquals("Booking successful!", display.getLastSuccess());
    }

    @Test
    void notLoggedIn() {
        controller.setCurrentUser(null);
        display.addInput("123");
        display.addInput("6");

        controller.bookPerformance();

        assertEquals("You must be logged in to book a performance.", display.getLastError());
    }

    @Test
    void notStudent() {
        controller.setCurrentUser(fakeEP);
        display.addInput("123");
        display.addInput("6");

        controller.bookPerformance();

        assertEquals("You must be a student to book a performance.", display.getLastError());
    }

    @Test
    void wrongPerformanceId() {
        display.addInput("5698");
        display.addInput("123");
        display.addInput("6");

        controller.bookPerformance();

        assertEquals("Performance with given number does not exist", display.getLastError());
        assertEquals("Booking successful!", display.getLastSuccess());
    }

    @Test
    void nonTicketedEvent() {
        Event notTicket = new Event(1324598L, "Spring", EventType.Dance, false, fakeEP);
        Collection<String> names = new ArrayList<>();
        names.add("DJ");
        Performance noTicketPerformance = notTicket.createPerformance(
                675L,
                LocalDateTime.of(2005, 6, 8, 18, 0),
                LocalDateTime.of(2005, 7, 12, 4, 0),
                names, "1 george street", 200,
                false, false, 50, 15.00);

        Collection<Performance> none = new ArrayList<>();
        none.add(noTicketPerformance);
        BookingController newController = new BookingController(display, none, new fakePayment(true));
        newController.setCurrentUser(student);

        display.addInput("675");

        newController.bookPerformance();
        assertEquals("The requested performance's event is not ticketed. There is no need to book it.", display.getLastError());
        assertNull(display.getLastSuccess());
    }

    @Test
    void noTicketsisHigherthanAvailable() {
        display.addInput("123");
        display.addInput("55");

        controller.bookPerformance();
        assertEquals("Requested performance does not have enough tickets.", display.getLastError());
        assertNull(display.getLastSuccess());
    }

    @Test
    void unsuccesfulPayment() {
        BookingController failingController = new BookingController(display, performances, new fakePayment(false));
        failingController.setCurrentUser(student);

        display.addInput("123");
        display.addInput("5");

        failingController.bookPerformance();
        assertEquals("There was an issue with payment.", display.getLastError());
        assertNull(display.getLastSuccess());
    }

    @Test
    void multipleWrongId() {
        display.addInput("245");
        display.addInput("592");
        display.addInput("123");
        display.addInput("5");

        controller.bookPerformance();

        assertEquals("Performance with given number does not exist", display.getLastError());
        assertEquals("Booking successful!", display.getLastSuccess());
    }
}