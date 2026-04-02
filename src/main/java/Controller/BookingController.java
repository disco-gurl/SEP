package Controller;
import External.MockPaymentSystem;
import External.PaymentSystem;
import Performance.Performance;
import User.Student;
import Booking.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import View.View;

public class BookingController extends Controller{
    private long nextBookingNumber;
    private final Collection<Performance> performances;
    private final PaymentSystem paymentSystem;

    public BookingController(View view, Collection<Performance> performances, PaymentSystem paymentSystem) {
        super(view);
        this.performances = performances;
        this.paymentSystem = paymentSystem;
    }

    public void bookPerformance() {
        //check if they are logged in
        if (currentUser == null) {
            view.displayError("You must be logged in to book a performance.");
            return;
        }

        if (!checkCurrentUserIsStudent()) {
            view.displayError("You must be a student to book a performance.");
            return;
        }
        Performance performance = null;
        boolean possibleBooking = false;

        while (!possibleBooking) {
            //check the id through all possible ids
            performance = null;
            long performanceID = Long.parseLong(view.getInput("Enter booking info: "));

            for (Performance p : performances) {
                if (p.getID() == performanceID) {
                    performance = p;
                    break;
                }
            }

            if (performance == null) {
                view.displayError("Performance with given number does not exist");
                ;
            } else {
                possibleBooking = true;
            }
        }

        //if it is not ticketed then terminates
        if (!performance.checkIfEventIsTicketed()) {
            view.displayError("The requested performance's event is not ticketed. There is no need to book it.");
            return;
        }

        int numTicketsRequested = Integer.parseInt(view.getInput("Enter number of tickets: "));

        //if the number of tickers wanted is more than available then terminate
        if (!performance.checkIfTicketsLeft(numTicketsRequested)) {
            view.displayError("Requested performance does not have enough tickets.");
            return;
        }

        Student student = (Student) currentUser;
        //collect information for student booking record
        Booking booking = new Booking(performance, student, numTicketsRequested);
        performance.addBooking(booking);

        String eventTitle = performance.getEventTitle();
        String studentEmail = student.getEmail();
        int studentPhone = student.getPhoneNumber();
        String epEmail = performance.getOrganiserEmail();
        double finalTicketPrice = performance.getFinalTicketPrice();
        double transactionAmount = finalTicketPrice * numTicketsRequested;

        boolean paymentSuccessful = paymentSystem.processPayment(
                numTicketsRequested, eventTitle, studentEmail, studentPhone, epEmail, transactionAmount);

        //check if payment is successful
        if (!paymentSuccessful) {
            view.displayError("There was an issue with payment.");
            booking.cancelPaymentFailed();
            return;
        }

        int numTicketsSold = performance.getNumTicketsSold();
        performance.setNumTicketsSold(numTicketsSold + numTicketsRequested);

        view.displaySuccess("Booking successful!");

        //create booking record
        String bookingRecord = booking.generateBookingRecord();
        view.displayBookingRecord(bookingRecord);

    }


    /**
     *  Review use case
     */
    public void reviewPerformance() {
        if (getCurrentUser() == null) {
            getView().displayError("You must be logged in to review a performance");
            return;
        }

        // Must be student for review according to requirements.
        if (!checkCurrentUserIsStudent()) {
            getView().displayError("You must be a student to review a performance.");
            return;
        }

        Student student = (Student) getCurrentUser();

        Performance performance = requestPerformance();
        if (performance == null) {
            return;
        }

        if (!studentReviewPossible(student, performance)) {
            return;
        }

        // get the review and submit it
        submitReview(performance);
    }

    public Performance requestPerformance() {
        String input = getView().getInput("Enter the performance ID: ");

        long performanceID;
        try {
            performanceID = Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            getView().displayError("Invalid peformance ID. ");
            return null;
        }

        Performance performance = getPerformanceByID(performanceID);

        if (performance == null) {
            getView().displayError("No performance found with " + performanceID);
        }

        return performance;
    }

    // Checks if review is possible after time has passed only and only if student has had a booking.
    private boolean studentReviewPossible(Student student, Performance performance) {
        if (performance.checkHasNotHappenedYet()) {
            getView().displayError("You can only review performancesthat have already taken place.");
            return false;
        }

        for (Booking b : student.getBookings()) {
            if (b.getPerformanceID() == performance.getID()) {
                return true;
            }
        }
        getView().displayError("You can only review performances you have booked.");
        return false;
    }

    private void submitReview(Performance performance) {
        String Input = getView().getInput("Enter the rating, 1 to 5");
        int rating;
        try {
            rating = Integer.parseInt(Input.trim());
        } catch (NumberFormatException e) {
            getView().displayError("Invalid rating format.");
            return;
        }
        if (rating < 1 || rating > 5) {
            getView().displayError("Rating must be between 1 and 5.");
            return;
        }

        String comment = getView().getInput("Enter a comment (or press Enter to skip): ");

        performance.review(rating, comment);
        getView().displaySuccess("Your review has been submitted successfully.");
    }

    public Performance getPerformanceByID(long performanceID) {
        for (Performance p: performances) {
            if (p.getID() == performanceID) {
                return p;
            }
        }
        return null;
    }

    public void cancelBooking(int bookingID){
        if (getCurrentUser() == null){
            getView().displayError("You must be logged in to cancel a booking.");
            return;
        }

        if (!checkCurrentUserIsStudent()){
            getView().displayError("You must be a student to cancel a booking.");
            return;
        }

        Student student = (Student) getCurrentUser();
        Booking foundBooking = null;

        for (Booking b : student.getBookings()) {
            if (b.getBookingNumber() == bookingID) {
                foundBooking = b;
                break;
            }
        }

        if (foundBooking == null) {
            getView().displayError("Booking not found.");
            return;
        }

        LocalDateTime start = foundBooking.getPerformance().getStartDateTime();
        LocalDateTime now = LocalDateTime.now();
        if (now.plusHours(24).isAfter(start)) {
            getView().displayError("Cannot cancel booking within 24 hours.");
            return;
        }

        boolean refunded = paymentSystem.processRefund(foundBooking.getNumTickets(),
                foundBooking.getPerformance().getEventTitle(),
                student.getEmail(),
                student.getPhoneNumber(),
                foundBooking.getPerformance().getEvent().getOrganiserEmail(),
                foundBooking.getAmountPaid(),
                null);

        if (!refunded) {
            getView().displayError("Refund unsuccessful.");
            return;
        }

        foundBooking.cancelByStudent();
        getView().displaySuccess("Booking has been cancelled.");
    }
}

