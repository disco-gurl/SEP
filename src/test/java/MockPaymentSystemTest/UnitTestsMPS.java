package MockPaymentSystemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import External.MockPaymentSystem;

public class UnitTestsMPS {

    private MockPaymentSystem ps;

    @BeforeEach
    void setUp() {
        ps = new MockPaymentSystem();
    }

    // processPayment tests

    // Should return true when all inputs are valid
    @Test
    void testPaymentWorksNormally() {
        boolean res = ps.processPayment(2, "Jazz Night", "student@ed.ac.uk",
                1234567890, "ep@provider.com", 25.0);
        assertTrue(res, "should work with normal valid inputs");
    }

    // Buying just 1 ticket should still be fine
    @Test
    void testPaymentOneTicket() {
        assertTrue(ps.processPayment(1, "Comedy Show", "s@ed.ac.uk",
                        1111111111, "ep@test.com", 10.0),
                "buying 1 ticket should be fine");
    }

    // Should return false when student email is null
    @Test
    void testPaymentNullStudentEmail() {
        boolean res = ps.processPayment(2, "Jazz Night", null,
                1234567890, "ep@provider.com", 25.0);
        assertFalse(res, "null student email should fail");
    }

    // Should return false when ep email is null
    @Test
    void testPaymentNullEpEmail() {
        assertFalse(ps.processPayment(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, null, 25.0),
                "null ep email should fail");
    }

    // Should return false when event title is null
    @Test
    void testPaymentNullTitle() {
        assertFalse(ps.processPayment(2, null, "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0),
                "null event title should fail");
    }

    // All the string parameters are null at once, should definitely fail
    @Test
    void testPaymentAllNulls() {
        assertFalse(ps.processPayment(2, null, null,
                        1234567890, null, 25.0),
                "all null strings should definitely fail");
    }

    // Should return false when numTickets is 0
    @Test
    void testPaymentZeroTickets() {
        assertFalse(ps.processPayment(0, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0),
                "0 tickets shouldnt be allowed");
    }

    // Negative tickets makes no sense, should return false
    @Test
    void testPaymentNegativeTickets() {
        assertFalse(ps.processPayment(-3, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0),
                "negative tickets makes no sense, should fail");
    }

    // Should return false when transaction amount is 0
    @Test
    void testPaymentZeroAmount() {
        assertFalse(ps.processPayment(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 0.0),
                "0 amount should fail");
    }

    // Should return false when transaction amount is negative
    @Test
    void testPaymentNegativeAmount() {
        assertFalse(ps.processPayment(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", -10.0),
                "negative amount should fail");
    }

    // Both tickets and amount are 0 so should fail
    @Test
    void testPaymentBothZero() {
        assertFalse(ps.processPayment(0, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 0.0),
                "both 0 tickets and 0 amount should fail");
    }

    // Multiple things wrong at once: null email and 0 tickets
    @Test
    void testPaymentNullEmailAndBadTickets() {
        assertFalse(ps.processPayment(0, "Jazz Night", null,
                        1234567890, "ep@provider.com", 25.0),
                "null email + 0 tickets, should fail either way");
    }

    // Large but valid inputs should still return true
    @Test
    void testPaymentBigNumbers() {
        assertTrue(ps.processPayment(10000, "Big Festival", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 500000.0),
                "large but valid inputs should still work");
    }


    // processRefund tests

    // Should return true for valid inputs with an organiser message
    @Test
    void testRefundWorksNormally() {
        boolean res = ps.processRefund(2, "Jazz Night", "student@ed.ac.uk",
                1234567890, "ep@provider.com", 25.0, "Event cancelled due to weather");
        assertTrue(res, "refund with valid inputs and a message should work");
    }

    // organiserMsg can be null since its optional, should still return true
    @Test
    void testRefundNullMessage() {
        assertTrue(ps.processRefund(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0, null),
                "null organiser message should still work");
    }

    // Empty string message should also be fine, its not null
    @Test
    void testRefundEmptyMessage() {
        assertTrue(ps.processRefund(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0, ""),
                "empty string message should be fine too");
    }

    // Refunding 1 ticket should work
    @Test
    void testRefundOneTicket() {
        assertTrue(ps.processRefund(1, "Comedy Show", "s@ed.ac.uk",
                        1111111111, "ep@test.com", 10.0, "Sorry!"),
                "refunding 1 ticket should work");
    }

    // Should return false when student email is null
    @Test
    void testRefundNullStudentEmail() {
        assertFalse(ps.processRefund(2, "Jazz Night", null,
                        1234567890, "ep@provider.com", 25.0, "Cancelled"),
                "null student email should fail");
    }

    // Should return false when ep email is null
    @Test
    void testRefundNullEpEmail() {
        assertFalse(ps.processRefund(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, null, 25.0, "Cancelled"),
                "null ep email should fail");
    }

    // Should return false when event title is null
    @Test
    void testRefundNullTitle() {
        assertFalse(ps.processRefund(2, null, "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0, "Cancelled"),
                "null title should fail");
    }

    // All required strings are null so should fail
    @Test
    void testRefundAllNulls() {
        assertFalse(ps.processRefund(2, null, null,
                        1234567890, null, 25.0, null),
                "all required strings null should fail");
    }

    // Cant refund 0 tickets, should return false
    @Test
    void testRefundZeroTickets() {
        assertFalse(ps.processRefund(0, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0, "Cancelled"),
                "cant refund 0 tickets");
    }

    // Negative tickets shouldnt work
    @Test
    void testRefundNegativeTickets() {
        assertFalse(ps.processRefund(-1, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 25.0, "Cancelled"),
                "negative tickets shouldnt work");
    }

    // Refunding 0 amount doesnt make sense
    @Test
    void testRefundZeroAmount() {
        assertFalse(ps.processRefund(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 0.0, "Cancelled"),
                "refunding 0 amount doesnt make sense");
    }

    // Negative refund amount should return false
    @Test
    void testRefundNegativeAmount() {
        assertFalse(ps.processRefund(2, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", -15.0, "Cancelled"),
                "negative refund amount should fail");
    }

    // Both 0 tickets and 0 amount, both are bad
    @Test
    void testRefundBothZero() {
        assertFalse(ps.processRefund(0, "Jazz Night", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 0.0, "Cancelled"),
                "0 tickets and 0 amount both bad");
    }

    // Multiple things wrong: null email and negative tickets
    @Test
    void testRefundNullEmailAndBadTickets() {
        assertFalse(ps.processRefund(-2, "Jazz Night", null,
                        1234567890, "ep@provider.com", 25.0, "Cancelled"),
                "null email and negative tickets should fail");
    }

    // Large valid values should still return true
    @Test
    void testRefundBigNumbers() {
        assertTrue(ps.processRefund(5000, "Huge Event", "student@ed.ac.uk",
                        1234567890, "ep@provider.com", 250000.0, "We are very sorry"),
                "big values should still be fine");
    }
}