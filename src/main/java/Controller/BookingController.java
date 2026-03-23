package Controller;
import External.MockPaymentSystem;
import Performance.Performance;
import View.View;
import User.Student;
import Booking.Booking;
import java.util.Collection;

public class BookingController extends Controller{
    private long nextBookingNumber;
    private final Collection<Performance> performances;
    private final MockPaymentSystem paymentSystem;

    public BookingController(View view, Collection<Performance> performances, MockPaymentSystem paymentSystem) {
        super(view);
        this.performances = performances;
        this.paymentSystem = paymentSystem;
    }

    public void bookPerformance() {
        Performance performance = null;
        boolean possibleBooking = false;

        while (performance == null || !possibleBooking) {
            long performanceID = Long.parseLong(view.getInput("Enter booking info: "));

            for (Performance p : performances) {
                if (p.getID() == performanceID) {
                    performance = p;
                    break;
                }
            }

            if (performance == null) {
                view.displayError("Performance with given number does not exist");
                continue;
            }

            boolean isTicketed = performance.checkIfEventIsTicketed();
            if (!isTicketed) {
                view.displayError("The requested performance's event is not ticketed. There is no need to book it.");
                continue;
            }

            int numTicketsRequested = Integer.parseInt(view.getInput("Enter number of tickets: "));
            boolean enoughTickets = performance.checkIfTicketsLeft(numTicketsRequested);
            if (!enoughTickets) {
                view.displayError("Requested performance has no tickets left.");
                continue;
            }

            possibleBooking = true;

            Student student = (Student) currentUser;

            Booking booking = new Booking(performance, student, numTicketsRequested);

            performance.addBooking(booking);

            String eventTitle = performance.getEventTitle();
            String studentEmail = student.email;
            int studentPhone = student.phoneNumber;
            String epEmail = performance.getOrganiserEmail();
            double finalTicketPrice = performance.getFinalTicketPrice();
            double transactionAmount = finalTicketPrice * numTicketsRequested;

            boolean paymentSuccessful = paymentSystem.processPayment(
                    numTicketsRequested, eventTitle, studentEmail, studentPhone, epEmail, transactionAmount
            );

            if (!paymentSuccessful) {
                view.displayError("There was an issue with payment.");
                booking.cancelPaymentFailed();
                return;
            }

            int numTicketsSold = performance.getNumTicketsSold();
            performance.setNumTicketsSold(numTicketsSold + numTicketsRequested);

            view.displaySuccess("Booking successful!");

            String bookingRecord = booking.generateBookingRecord();
            view.displayBookingRecord(bookingRecord);
        }
    }
}
