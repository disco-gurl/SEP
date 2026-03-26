package Booking;

import Performance.Performance;
import User.Student;
import java.time.LocalDateTime;

public class Booking {
    private long bookingNumber;
    private int numTickets;
    private double amountPaid;
    private BookingStatus status;
    private LocalDateTime bookingDateTime;
    private Performance performance;
    private Student student;

    public Booking(Performance performance, Student student, int numTicketsRequested) {
        this.performance = performance;
        this.student = student;
        this.numTickets = numTicketsRequested;
        this.amountPaid = performance.getFinalTicketPrice() * numTicketsRequested;
        this.bookingDateTime = LocalDateTime.now();
        this.bookingNumber = System.currentTimeMillis();
        this.status = BookingStatus.ACTIVE;
    }

    public void cancelByStudent() {
        this.status = BookingStatus.CANCELLEDBYSTUDENT;
    }

    public void cancelPaymentFailed() {
        this.status = BookingStatus.PAYMENTFAILED;
    }

    public void cancelByProvider() {
        this.status = BookingStatus.CANCELLEDBYPROVIDER;
    }

    public boolean checkBookedByStudent(String email) {
        return student.getEmail().equals(email);
    }

    public String getStudentDetails() {
        return "Name: " + student.getName() +
                "\nEmail: " + student.getEmail() +
                "\nPhone: " + student.getPhoneNumber();
    }

    public String generateBookingRecord() {
        return "Booking Number: " + bookingNumber +
                "\nStudent Name: " + student.getName() +
                "\nStudent Email: " + student.getEmail() +
                "\nStudent Phone: " + student.getPhoneNumber() +
                "\nNumber of Tickets: " + numTickets +
                "\nAmount Paid: " + amountPaid +
                "\nBooking Date: " + bookingDateTime +
                "\nStatus: " + status +
                "\n" + performance.toString();
    }

    public long getPerformanceID() { return performance.getID(); }

    public long getBookingNumber() { return bookingNumber; }

    public Performance getPerformance() { return performance; }

    public int getNumTickets() { return numTickets; }

    public double getAmountPaid() { return amountPaid; }
}
