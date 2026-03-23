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
        return student.email.equals(email);
    }

    public String getStudentDetails() {
        return "Name: " + student.name +
                "\nEmail: " + student.email +
                "\nPhone: " + student.phoneNumber;
    }

    public String generateBookingRecord() {
        return "Booking Number: " + bookingNumber +
                "\nStudent Name: " + student.name +
                "\nStudent Email: " + student.email +
                "\nStudent Phone: " + student.phoneNumber +
                "\nNumber of Tickets: " + numTickets +
                "\nAmount Paid: " + amountPaid +
                "\nBooking Date: " + bookingDateTime +
                "\nStatus: " + status +
                "\n" + performance.toString();
    }
}
