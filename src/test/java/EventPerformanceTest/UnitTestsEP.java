package EventPerformanceTest;

import Event.Event;
import Event.EventType;
import Performance.Performance;
import Performance.PerformanceStatus;
import User.EntertainmentProvider;
import User.Student;
import Booking.Booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTestsEP {
    private EntertainmentProvider ep;
    private Event ticketedEvent;
    private Event freeEvent;
    private Performance futurePerformance;
    private Performance pastPerformance;
    private Performance freePerformance;
    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;
    private List<String> performers;

    @BeforeEach
    public void setUp() {
        ep = new EntertainmentProvider(
                "ep@musicco.com", "eppass99",
                "Music Co", "1234567890",
                "Bob Jones", "A music events company"
        );

        ticketedEvent = new Event(1L, "Rock Concert", EventType.Music, true, ep);
        freeEvent = new Event(2L, "Free Film Night", EventType.Movie, false, ep);

        performers = Arrays.asList("Artist A", "Artist B");

        futureStart = LocalDateTime.now().plusDays(14);
        futureEnd = futureStart.plusHours(3);
        LocalDateTime pastStart = LocalDateTime.now().minusDays(14);
        LocalDateTime pastEnd = pastStart.plusHours(3);

        // A standard ticketed future performance with 100 tickets at £25
        futurePerformance = new Performance(
                101L, futureStart, futureEnd, performers,
                "Main Hall, Edinburgh", 500, false, false,
                100, 25.00, ticketedEvent
        );
        ticketedEvent.addPerformance(futurePerformance);

        // A past performance (already happened) for the same event
        pastPerformance = new Performance(
                102L, pastStart, pastEnd, performers,
                "Small Hall, Edinburgh", 200, true, true,
                50, 15.00, ticketedEvent
        );
        ticketedEvent.addPerformance(pastPerformance);

        // A performance for a free (non-ticketed) event
        freePerformance = new Performance(
                201L, futureStart, futureEnd, performers,
                "Meadows Park", 1000, true, false,
                0, 0.00, freeEvent
        );
        freeEvent.addPerformance(freePerformance);
    }

    // createPerformance should add the performance to the event's collection
    @Test
    public void testCreatePerformanceAddsToEvent() {
        Event event = new Event(10L, "Test Event", EventType.Dance, true, ep);
        event.createPerformance(
                999L, futureStart, futureEnd, performers,
                "Venue X", 100, false, false, 50, 10.00);
        assertEquals(1, event.getPerformances().size(),
                "Event should have 1 performance after calling createPerformance()");
    }

    // The returned performance should have the correct ID
    @Test
    public void testCreatePerformanceReturnsCorrectID() {
        Event event = new Event(10L, "Test Event", EventType.Dance, true, ep);
        Performance p = event.createPerformance(
                777L, futureStart, futureEnd, performers,
                "Venue X", 100, false, false, 50, 10.00);
        assertEquals(777L, p.getID(),
                "Created performance should have the ID that was passed in");
    }

    // Should find a performance that exists in this event
    @Test
    public void testGetPerformanceByIDFindsExisting() {
        Performance found = ticketedEvent.getPerformanceByID(101L);
        assertNotNull(found,
                "Should find a performance when searching with a valid existing ID");
    }

    // Should return null for an ID that doesn't belong to this event
    @Test
    public void testGetPerformanceByIDReturnsNullForNonExistent() {
        Performance found = ticketedEvent.getPerformanceByID(999L);
        assertNull(found,
                "Should return null when searching for a non-existent performance ID");
    }

    // Should return null when the event has no performances at all
    @Test
    public void testGetPerformanceByIDOnEmptyEvent() {
        Event emptyEvent = new Event(50L, "Empty", EventType.Sports, true, ep);
        assertNull(emptyEvent.getPerformanceByID(1L),
                "Should return null when searching an event with no performances");
    }

    // Should return 0 when no reviews have been submitted
    @Test
    public void testAverageRatingZeroWhenNoReviews() {
        assertEquals(0.0, ticketedEvent.getAverageRatingOfPerformances(),
                "Average rating should be 0.0 when there are no reviews");
    }

    // Average of one rating of 4 should be 4.0
    @Test
    public void testAverageRatingWithSingleReview() {
        futurePerformance.review(4, "Good show");
        assertEquals(4.0, ticketedEvent.getAverageRatingOfPerformances(),
                "Average rating should equal the single review's rating");
    }

    // Average should work across multiple performances: (5 + 3) / 2 = 4.0
    @Test
    public void testAverageRatingAcrossMultiplePerformances() {
        futurePerformance.review(5, "Amazing");
        pastPerformance.review(3, "OK");
        assertEquals(4.0, ticketedEvent.getAverageRatingOfPerformances(),
                "Average of ratings 5 and 3 across two performances should be 4.0");
    }

    // Should return an empty collection when no reviews exist
    @Test
    public void testAllReviewsEmptyWhenNoReviews() {
        assertTrue(ticketedEvent.getAllPerformanceReviews().isEmpty(),
                "Reviews should be empty when no reviews have been submitted");
    }

    // Should include the rating value in the review string
    @Test
    public void testAllReviewsContainsRating() {
        futurePerformance.review(4, "Nice");
        String review = ticketedEvent.getAllPerformanceReviews().iterator().next();
        assertTrue(review.contains("Rating: 4"),
                "Review string should contain the rating value");
    }

    // Should include the comment when one is provided
    @Test
    public void testAllReviewsIncludesComment() {
        futurePerformance.review(5, "Wonderful show");
        String review = ticketedEvent.getAllPerformanceReviews().iterator().next();
        assertTrue(review.contains("Comment: Wonderful show"),
                "Review string should include the comment text when provided");
    }

    // Should omit the comment section when the comment is empty
    @Test
    public void testAllReviewsOmitsEmptyComment() {
        futurePerformance.review(3, "");
        String review = ticketedEvent.getAllPerformanceReviews().iterator().next();
        assertFalse(review.contains("Comment:"),
                "Review string should not include 'Comment:' when the comment is empty");
    }

    // Should detect an exact time overlap with an existing performance
    @Test
    public void testHasPerformanceAtSameTimesDetectsOverlap() {
        assertTrue(ticketedEvent.hasPerformanceAtSameTimes(futureStart, futureEnd),
                "Should detect overlap when times exactly match an existing performance");
    }

    // Should detect a partial overlap (starts during an existing performance)
    @Test
    public void testHasPerformanceAtSameTimesDetectsPartialOverlap() {
        LocalDateTime midStart = futureStart.plusHours(1);
        LocalDateTime midEnd = futureEnd.plusHours(1);
        assertTrue(ticketedEvent.hasPerformanceAtSameTimes(midStart, midEnd),
                "Should detect a partial time overlap with an existing performance");
    }

    // Should return false when the proposed time doesn't overlap any performance
    @Test
    public void testHasPerformanceAtSameTimesNoOverlap() {
        LocalDateTime laterStart = futureEnd.plusDays(5);
        LocalDateTime laterEnd = laterStart.plusHours(2);
        assertFalse(ticketedEvent.hasPerformanceAtSameTimes(laterStart, laterEnd),
                "Should return false when there is no time overlap with any performance");
    }

    // A performance ending in the future should return true
    @Test
    public void testCheckHasNotHappenedYetTrueForFuture() {
        assertTrue(futurePerformance.checkHasNotHappenedYet(),
                "A future performance should return true for checkHasNotHappenedYet()");
    }

    // A performance that ended in the past should return false
    @Test
    public void testCheckHasNotHappenedYetFalseForPast() {
        assertFalse(pastPerformance.checkHasNotHappenedYet(),
                "A past performance should return false for checkHasNotHappenedYet()");
    }

    // Should return true when the email matches the organiser
    @Test
    public void testCheckCreatedByEPTrueForMatchingEmail() {
        assertTrue(futurePerformance.checkCreatedByEP("ep@musicco.com"),
                "Should return true when email matches the organiser's email");
    }

    // Should return false for a completely different email
    @Test
    public void testCheckCreatedByEPFalseForWrongEmail() {
        assertFalse(futurePerformance.checkCreatedByEP("other@provider.com"),
                "Should return false for an email that doesn't match the organiser");
    }

    // Email comparison is case-sensitive via String.equals()
    @Test
    public void testCheckCreatedByEPIsCaseSensitive() {
        assertFalse(futurePerformance.checkCreatedByEP("EP@MUSICCO.COM"),
                "checkCreatedByEP uses equals(), so uppercase should not match");
    }

    // Should return true for a performance whose event is ticketed
    @Test
    public void testCheckIfEventIsTicketedTrue() {
        assertTrue(futurePerformance.checkIfEventIsTicketed(),
                "Should return true for a ticketed event's performance");
    }

    // Should return false for a performance whose event is free
    @Test
    public void testCheckIfEventIsTicketedFalseForFreeEvent() {
        assertFalse(freePerformance.checkIfEventIsTicketed(),
                "Should return false for a non-ticketed event's performance");
    }

    // Requesting fewer than available should return true
    @Test
    public void testCheckIfTicketsLeftTrueWhenEnough() {
        assertTrue(futurePerformance.checkIfTicketsLeft(50),
                "Should return true when requesting fewer tickets than available");
    }

    // Requesting exactly the remaining number should return true (boundary)
    @Test
    public void testCheckIfTicketsLeftTrueAtExactBoundary() {
        assertTrue(futurePerformance.checkIfTicketsLeft(100),
                "Should return true when requesting exactly the remaining tickets");
    }

    // Requesting more than available should return false
    @Test
    public void testCheckIfTicketsLeftFalseWhenNotEnough() {
        assertFalse(futurePerformance.checkIfTicketsLeft(101),
                "Should return false when requesting more tickets than available");
    }

    // After some tickets are sold, the remaining count should be respected
    @Test
    public void testCheckIfTicketsLeftAfterSomeSold() {
        futurePerformance.setNumTicketsSold(95);
        assertFalse(futurePerformance.checkIfTicketsLeft(10),
                "Should return false when only 5 tickets remain but 10 are requested");
    }

    // When all tickets are sold, even 1 ticket should be unavailable
    @Test
    public void testCheckIfTicketsLeftFalseWhenSoldOut() {
        futurePerformance.setNumTicketsSold(100);
        assertFalse(futurePerformance.checkIfTicketsLeft(1),
                "Should return false when all tickets have been sold");
    }

    // Without sponsorship, the final price should equal the base price
    @Test
    public void testGetFinalTicketPriceWithoutSponsorship() {
        assertEquals(25.00, futurePerformance.getFinalTicketPrice(),
                "Final price should equal the base price when not sponsored");
    }

    // With partial sponsorship, the price should be reduced accordingly
    @Test
    public void testGetFinalTicketPriceWithPartialSponsorship() {
        futurePerformance.sponsor(10.00);
        assertEquals(15.00, futurePerformance.getFinalTicketPrice(),
                "Final price should be base minus sponsorship (25 - 10 = 15)");
    }

    // Sponsorship equal to the ticket price should result in £0
    @Test
    public void testGetFinalTicketPriceWithFullSponsorship() {
        futurePerformance.sponsor(25.00);
        assertEquals(0.00, futurePerformance.getFinalTicketPrice(),
                "Final price should be £0.00 when sponsorship equals ticket price");
    }

    // Sponsorship exceeding the ticket price should still cap at £0 (not go negative)
    @Test
    public void testGetFinalTicketPriceDoesNotGoNegative() {
        futurePerformance.sponsor(50.00);
        assertEquals(0.00, futurePerformance.getFinalTicketPrice(),
                "Final price should not go below £0.00 even when sponsorship exceeds price");
    }

    // After sponsoring, the performance should be marked as sponsored
    @Test
    public void testSponsorSetsIsSponsoredTrue() {
        futurePerformance.sponsor(10.00);
        assertTrue(futurePerformance.isSponsored(),
                "After sponsor(), isSponsored() should return true");
    }

    // The stored sponsorship amount should match what was passed in
    @Test
    public void testSponsorStoresCorrectAmount() {
        futurePerformance.sponsor(12.50);
        assertEquals(12.50, futurePerformance.getSponsoredAmount(),
                "Sponsored amount should match the value passed to sponsor()");
    }

    // setStatus should change the status to CANCELLED
    @Test
    public void testSetStatusToCancelled() {
        futurePerformance.setStatus(PerformanceStatus.CANCELLED);
        assertEquals(PerformanceStatus.CANCELLED, futurePerformance.getStatus(),
                "After setStatus(CANCELLED), status should be CANCELLED");
    }

    // Changing one performance's status should not affect another
    @Test
    public void testSetStatusDoesNotAffectOtherPerformances() {
        futurePerformance.setStatus(PerformanceStatus.CANCELLED);
        assertEquals(PerformanceStatus.ACTIVE, pastPerformance.getStatus(),
                "Cancelling one performance should not change another's status");
    }

    // A new performance should start as ACTIVE
    @Test
    public void testNewPerformanceHasActiveStatus() {
        assertEquals(PerformanceStatus.ACTIVE, futurePerformance.getStatus(),
                "A newly created performance should have ACTIVE status");
    }

    // After submitting a review, the ratings collection should grow by one
    @Test
    public void testReviewAddsOneRating() {
        futurePerformance.review(4, "Good");
        assertEquals(1, futurePerformance.getReviewRatings().size(),
                "Should have 1 rating after submitting 1 review");
    }

    // The stored rating should match what was submitted
    @Test
    public void testReviewStoresCorrectRating() {
        futurePerformance.review(5, "Excellent");
        int storedRating = futurePerformance.getReviewRatings().iterator().next();
        assertEquals(5, storedRating,
                "Stored rating should match the submitted value of 5");
    }

    // Multiple reviews should accumulate, not overwrite
    @Test
    public void testMultipleReviewsAccumulate() {
        futurePerformance.review(5, "Great");
        futurePerformance.review(2, "Disappointing");
        futurePerformance.review(4, "Decent");
        assertEquals(3, futurePerformance.getReviewRatings().size(),
                "Should have 3 ratings after submitting 3 reviews");
    }

    // The comment should also be stored correctly
    @Test
    public void testReviewStoresCorrectComment() {
        futurePerformance.review(5, "Excellent");
        String storedComment = futurePerformance.getReviewComments().iterator().next();
        assertEquals("Excellent", storedComment,
                "Stored comment should match the submitted text");
    }

    // Performance: addBooking() tests

    // After adding a booking, it should appear in the bookings collection
    @Test
    public void testAddBookingIncreasesBookingCount() {
        Student student = new Student("alice@uni.ac.uk", "pass", "Alice", 1234567);
        Booking booking = new Booking(futurePerformance, student, 2);
        futurePerformance.addBooking(booking);
        assertEquals(1, futurePerformance.getBookings().size(),
                "Bookings collection should have 1 entry after adding a booking");
    }

    // Adding multiple bookings should all be stored
    @Test
    public void testAddMultipleBookings() {
        Student student1 = new Student("alice@uni.ac.uk", "pass", "Alice", 1234567);
        Student student2 = new Student("bob@uni.ac.uk", "pass", "Bob", 7654321);
        futurePerformance.addBooking(new Booking(futurePerformance, student1, 1));
        futurePerformance.addBooking(new Booking(futurePerformance, student2, 3));
        assertEquals(2, futurePerformance.getBookings().size(),
                "Bookings collection should have 2 entries after adding 2 bookings");
    }

    // toString should contain the performance ID
    @Test
    public void testToStringContainsPerformanceID() {
        assertTrue(futurePerformance.toString().contains("101"),
                "toString() should contain the performance ID");
    }

    // toString should contain the event title
    @Test
    public void testToStringContainsEventTitle() {
        assertTrue(futurePerformance.toString().contains("Rock Concert"),
                "toString() should contain the parent event's title");
    }

    // toString should show ticket info for a ticketed event
    @Test
    public void testToStringShowsTicketPriceForTicketedEvent() {
        assertTrue(futurePerformance.toString().contains("Ticket Price"),
                "toString() should include ticket price information for a ticketed event");
    }

    // toString should show a non-ticketed message for a free event
    @Test
    public void testToStringShowsNonTicketedMessage() {
        assertTrue(freePerformance.toString().contains("non-ticketed"),
                "toString() should indicate non-ticketed for a free event performance");
    }

    // toString should indicate no reviews when none exist
    @Test
    public void testToStringShowsNoReviewsMessage() {
        assertTrue(futurePerformance.toString().contains("No reviews yet"),
                "toString() should show 'No reviews yet' when no reviews have been submitted");
    }
}
