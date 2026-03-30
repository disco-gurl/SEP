package BookingTest;

import Booking.Booking;
import Event.Event;
import Performance.Performance;
import User.EntertainmentProvider;
import User.Student;
import Event.EventType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Unit tests for the Booking class.
 *
 * These tests cover the constructor logic, the three cancellation methods,
 * checkBookedByStudent, getStudentDetails, and generateBookingRecord.
 *
 * Simple getters (getNumTickets, getPerformance, getBookingNumber) are not tested
 * as per the coursework guidelines.
 *
 * The Booking class doesn't have an existing method to fetch the status,
 * so cancellation tests verify the status change through the output of generateBookingRecord(),
 * which includes "Status: " followed by the status value.
 *
 */

public class UnitTestsBooking {

    private Student student;
    private EntertainmentProvider ep;
    private Event ticketedEvent;
    private Event freeEvent;

    // A standard ticketed performance at £20.00 with 50 tickets available
    private Performance ticketedPerformance;

    // A sponsored ticketed performance. Ticket price is £30.00 and the sponsorship
    // covers £10.00, so the final ticket price is £20.00
    private Performance sponsoredPerformance;

    // A performance whose ticket price is fully covered by sponsorship
    private Performance fullySponsoredPerformance;

    // A performance belonging to a non-ticketed (free) event
    private Performance freePerformance;

    // Sets up all shared objects before each test.
    // Students and admin staff are treated as pre-registered (hardcoded here)
    @BeforeEach
    public void setUp() {
        // Pre-registered student
        student = new Student("alice@uni.ac.uk", "password123", "Alice Smith",
                791112345);

        // Entertainment Provider
        ep = new EntertainmentProvider(
                "ep@musicco.com", "eppass99",
                "Music Co", "1234567890",
                "Bob Jones", "A music events company"
        );

        ticketedEvent = new Event(1L, "Summer Concert", EventType.Music, true, ep);
        freeEvent = new Event(2L, "Free Showcase", EventType.Music, false, ep);

        LocalDateTime futureStart = LocalDateTime.now().plusDays(30);
        LocalDateTime futureEnd = futureStart.plusHours(2);
        List<String> performers = Arrays.asList("Band A", "Band B");

        // Standard ticketed performance — £20.00 per ticket
        ticketedPerformance = new Performance(
                1001L, futureStart, futureEnd, performers,
                "City Hall, Edinburgh", 200, false, false,
                50, 20.00, ticketedEvent
        );

        // Sponsored performance — £30.00 ticket, £10.00 sponsorship → £20.00 final
        sponsoredPerformance = new Performance(
                1002L, futureStart, futureEnd, performers,
                "City Hall, Edinburgh", 200, false, false,
                50, 30.00, ticketedEvent
        );
        sponsoredPerformance.sponsor(10.00);

        // Fully sponsored — £15.00 ticket, £15.00 sponsorship → £0.00 final
        fullySponsoredPerformance = new Performance(
                1003L, futureStart, futureEnd, performers,
                "City Hall, Edinburgh", 200, false, false,
                50, 15.00, ticketedEvent
        );
        fullySponsoredPerformance.sponsor(15.00);

        // Free (non-ticketed) event performance
        freePerformance = new Performance(
                1004L, futureStart, futureEnd, performers,
                "City Hall, Edinburgh", 200, false, false,
                0, 0.00, freeEvent
        );
    }

    // Constructor tests = verifying the values set when a Booking is created

    // Booking fo 1 ticket at £20.00 -> amountPaid should be £20.00
    @Test
    public void testAmountPaidCorrectForSingleTicket() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertEquals(20.00, booking.getAmountPaid(),
                "Amount paid for 1 ticket at £20.00 should be £20.00");
    }

    // Booking for 3 tickets at £20.00 -> amountPaid should be £60.00
    @Test
    public void testAmountPaidCorrectForMultipleTickets() {
        Booking booking = new Booking(ticketedPerformance, student, 3);
        assertEquals(60.00, booking.getAmountPaid(),
                "Amount paid for 3 tickets at £20.00 should be £60.00");
    }

    // When a performance is sponsored, the booking should use the discounted final price,
    // not the original price.
    @Test
    public void testAmountPaidUsesDiscountedPriceForSponsoredPerformance() {
        Booking booking = new Booking(sponsoredPerformance, student, 2);
        assertEquals(40.00, booking.getAmountPaid(),
                "Amount paid should use the sponsored (discounted) price: 2 × £20.00 = £40.00");
    }

    // When a performance is fully sponsored, the booking's amountPaid should be £0.00.
    @Test
    public void testAmountPaidIsZeroForFullySponsoredPerformance() {
        Booking booking = new Booking(fullySponsoredPerformance, student, 2);
        assertEquals(0.00, booking.getAmountPaid(),
                "Amount paid should be £0.00 when the performance is fully sponsored");
    }

    // A newly created booking should have its number of tickets stored correctly.
    // We check 5 tickets here as a representative non-trivial value.
    @Test
    public void testNumTicketsStoredCorrectlyInConstructor() {
        Booking booking = new Booking(ticketedPerformance, student, 5);
        assertEquals(5, booking.getNumTickets(),
                "Booking should store the number of tickets requested (5)");
    }

    // Booking number is generated from System.currentTimeMillis() so it must be a positive long value.
    @Test
    public void testBookingNumberIsPositive() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertTrue(booking.getBookingNumber() > 0,
                "Booking number should be a positive long value");
    }

    // Two bookings created consecutively should have different booking numbers.
    @Test
    public void testBookingNumbersAreDifferentForDifferentBookings() throws InterruptedException {
        Booking booking1 = new Booking(ticketedPerformance, student, 1);
        // Small sleep to ensure different millisecond timestamps
        Thread.sleep(2);
        Booking booking2 = new Booking(ticketedPerformance, student, 1);
        assertNotEquals(booking1.getBookingNumber(), booking2.getBookingNumber(),
                "Two separately created bookings should have different booking numbers");
    }

    // A new booking should show ACTIVE status in its record,
    // confirming the constructor sets the initial status correctly.
    @Test
    public void testNewBookingHasActiveStatusInRecord() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertTrue(booking.generateBookingRecord().contains("ACTIVE"),
                "A newly created booking's record should show status ACTIVE");
    }


    // cancelByStudent tests

    // After cancelByStudent is called, the booking record should reflect the CANCLLEDBYSTUDENT status.
    @Test
    public void testCancelByStudentChangesStatusInRecord() {
        Booking booking = new Booking(ticketedPerformance, student, 2);
        booking.cancelByStudent();
        assertTrue(booking.generateBookingRecord().contains("CANCELLEDBYSTUDENT"),
                "After cancelByStudent(), the booking record should show CANCELLEDBYSTUDENT");
    }

    // Cancelling a booking should not change the amountPaid value.
    // The refund is handled separately by the payment system, not by this method.
    @Test
    public void testCancelByStudentDoesNotChangeAmountPaid() {
        Booking booking = new Booking(ticketedPerformance, student, 2);
        double amountBeforeCancel = booking.getAmountPaid();
        booking.cancelByStudent();
        assertEquals(amountBeforeCancel, booking.getAmountPaid(),
                "cancelByStudent() should not alter the amount paid stored in the booking");
    }

    // After cancelByStudent(), the record should no longer show ACTIVE.
    @Test
    public void testCancelByStudentRemovesActiveFromRecord() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        booking.cancelByStudent();
        assertFalse(booking.generateBookingRecord().contains("Status: ACTIVE"),
                "After cancelByStudent(), the record should no longer show Status: ACTIVE");
    }


    // cancelPaymentFailed() tests

    // After cancelPaymentFailed(), the booking record should reflect the PAYMENTFAILED status.
    @Test
    public void testCancelPaymentFailedChangesStatusInRecord() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        booking.cancelPaymentFailed();
        assertTrue(booking.generateBookingRecord().contains("PAYMENTFAILED"),
                "After cancelPaymentFailed(), the booking record should show PAYMENTFAILED");
    }

    // cancelPaymentFailed() should not alter the amountPaid stored in the booking.
    @Test
    public void testCancelPaymentFailedDoesNotChangeAmountPaid() {
        Booking booking = new Booking(ticketedPerformance, student, 3);
        double amountBefore = booking.getAmountPaid();
        booking.cancelPaymentFailed();
        assertEquals(amountBefore, booking.getAmountPaid(),
                "cancelPaymentFailed() should not alter the amount paid stored in the booking");
    }


    // cancelByProvider() tests

    // After the cancelByProivder, the booking record should reflect the CANCELLEDBYPROVIDER status.
    @Test
    public void testCancelByProviderChangesStatusInRecord() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        booking.cancelByProvider();
        assertTrue(booking.generateBookingRecord().contains("CANCELLEDBYPROVIDER"),
                "After cancelByProvider(), the booking record should show CANCELLEDBYPROVIDER");
    }

    // cancelByProvider should not alter the amoutnPaid stored in the booking.
    @Test
    public void testCancelByProviderDoesNotChangeAmountPaid() {
        Booking booking = new Booking(ticketedPerformance, student, 4);
        double amountBefore = booking.getAmountPaid();
        booking.cancelByProvider();
        assertEquals(amountBefore, booking.getAmountPaid(),
                "cancelByProvider() should not alter the amount paid stored in the booking");
    }


    // checkBookedByStudent(String email) tests

    // checkBookedByStudent() should return true when the email matches the student who made the booking.
    @Test
    public void testCheckBookedByStudentReturnsTrueForCorrectEmail() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertTrue(booking.checkBookedByStudent("alice@uni.ac.uk"),
                "checkBookedByStudent() should return true for the student's own email");
    }

    // checkBookedByStudent() should return false for an empty string, since no student has an empty email address.
    @Test
    public void testCheckBookedByStudentReturnsFalseForEmptyString() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertFalse(booking.checkBookedByStudent(""),
                "checkBookedByStudent() should return false for an empty email string");
    }

    // checkBookedByStudent() uses String.equals(), which is case-sensitive.
    // So an email with different capitalization should return false.
    @Test
    public void testCheckBookedByStudentIsCaseSensitive() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertFalse(booking.checkBookedByStudent("ALICE@UNI.AC.UK"),
                "checkBookedByStudent() is case-sensitive; uppercase version should return false");
    }

    // checkBookedByStudent() should return false for a partial email
    // that is a substring of the real email but is not an exact match.
    @Test
    public void testCheckBookedByStudentReturnsFalseForPartialEmail() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertFalse(booking.checkBookedByStudent("alice"),
                "checkBookedByStudent() should return false for a partial email match");
    }


    // getStudentDetails() tests

    // getStudentDetails() should include the student's name.
    @Test
    public void testGetStudentDetailsContainsName() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertTrue(booking.getStudentDetails().contains("Alice Smith"),
                "getStudentDetails() should contain the student's name");
    }

    // getStudentDetails() should include the student's email address.
    @Test
    public void testGetStudentDetailsContainsEmail() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertTrue(booking.getStudentDetails().contains("alice@uni.ac.uk"),
                "getStudentDetails() should contain the student's email address");
    }

    // getStudentDetails() should include the student's phone number.
    @Test
    public void testGetStudentDetailsContainsPhoneNumber() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertTrue(booking.getStudentDetails().contains("7911123456"),
                "getStudentDetails() should contain the student's phone number");
    }


    // generateBookingRecord() tests

    // generateBookingRecord() should include the student's name.
    @Test
    public void testGenerateBookingRecordContainsStudentName() {
        Booking booking = new Booking(ticketedPerformance, student, 2);
        assertTrue(booking.generateBookingRecord().contains("Alice Smith"),
                "generateBookingRecord() should include the student's name");
    }

    // generateBookingRecord() should include the student's email address.
    @Test
    public void testGenerateBookingRecordContainsStudentEmail() {
        Booking booking = new Booking(ticketedPerformance, student, 2);
        assertTrue(booking.generateBookingRecord().contains("alice@uni.ac.uk"),
                "generateBookingRecord() should include the student's email address");
    }

    // generateBookingRecord() should display the amount paid.
    @Test
    public void testGenerateBookingRecordContainsAmountPaid() {
        Booking booking = new Booking(ticketedPerformance, student, 2);
        assertTrue(booking.generateBookingRecord().contains("40.0"),
                "generateBookingRecord() should contain the total amount paid (40.0)");
    }

    // generateBookingRecord() should include the number of tickets booked.
    @Test
    public void testGenerateBookingRecordContainsNumberOfTickets() {
        Booking booking = new Booking(ticketedPerformance, student, 3);
        assertTrue(booking.generateBookingRecord().contains("3"),
                "generateBookingRecord() should contain the number of tickets (3)");
    }

    // generateBookingRecord() should include the booking number,
    // confirming the record is associated with this specific booking.
    @Test
    public void testGenerateBookingRecordContainsBookingNumber() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        String record = booking.generateBookingRecord();
        assertTrue(record.contains(String.valueOf(booking.getBookingNumber())),
                "generateBookingRecord() should contain the booking's own booking number");
    }

    // generateBookingRecord() should include the event title so the student
    // can identify which event they booked.
    @Test
    public void testGenerateBookingRecordContainsEventTitle() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        assertTrue(booking.generateBookingRecord().contains("Summer Concert"),
                "generateBookingRecord() should contain the event title");
    }

    // generateBookingRecord() should not be null or empty, it must always return a non-empty string.
    @Test
    public void testGenerateBookingRecordIsNotEmpty() {
        Booking booking = new Booking(ticketedPerformance, student, 1);
        String record = booking.generateBookingRecord();
        assertNotNull(record, "generateBookingRecord() should not return null");
        assertFalse(record.isBlank(), "generateBookingRecord() should not return a blank string");
    }

}
