package EPs;

import Controller.UserController;
import External.VerificationService;
import User.EntertainmentProvider;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class RegisterEntertainmentProviderSystemTests {

    private FakeView display;
    private UserController controller;

    static class FakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String lastSuccess;
        private String lastError;

        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return lastSuccess; }
        public String getLastError() { return lastError; }

        @Override
        public String getInput(String inputPrompt) { return inputs.poll(); }

        @Override
        public void displaySuccess(String successMessage) { this.lastSuccess = successMessage; }

        @Override
        public void displayError(String errorMessage) { this.lastError = errorMessage; }

        @Override
        public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {}

        @Override
        public void displaySpecificPerformance(String performanceInfo) {}

        @Override
        public void displayBookingRecord(String bookingRecord) {}
    }

    static class FakeVerificationService implements VerificationService {
        @Override
        public boolean verifyEntertainmentProvider(String businessRegistrationNumber) {
            return businessRegistrationNumber != null
                    && businessRegistrationNumber.length() == 10;
        }
    }

    static class RejectingVerificationService implements VerificationService {
        @Override
        public boolean verifyEntertainmentProvider(String businessRegistrationNumber) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        display = new FakeView();
        controller = new UserController(display, new FakeVerificationService());
    }

    @Test
    void allFieldsValid() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");

        controller.registerEntertainmentProvider();

        assertEquals("Edinburgh Festivals Ltd has been successfully registered.",
                display.getLastSuccess(),
                "Valid registration with all correct fields should succeed");
    }

    @Test
    void successfulRegistrationLogIn() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        controller.login();

        assertEquals("Login successful", display.getLastSuccess(),
                "After successful registration, EP should be able to log in");
    }

    @Test
    void emptyOrgName() {
        display.addInput("");

        controller.registerEntertainmentProvider();

        assertEquals("The organisation name cannot be empty", display.getLastError(),
                "Registration should fail when org name is empty");
    }

    @Test
    void nullOrgName() {
        display.addInput(null);

        controller.registerEntertainmentProvider();

        assertEquals("The organisation name cannot be empty", display.getLastError(),
                "Registration should fail when org name is null");
    }

    @Test
    void whitespaceOnlyOrgName() {
        display.addInput("   ");

        controller.registerEntertainmentProvider();

        assertEquals("The organisation name cannot be empty", display.getLastError(),
                "Registration should fail when org name is just whitespace");
    }

    @Test
    void emptyBusinessNumber() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("");

        controller.registerEntertainmentProvider();

        assertEquals("The business number cannot be empty", display.getLastError(),
                "Registration should fail when business number is empty");
    }

    @Test
    void emptyContactName() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("");

        controller.registerEntertainmentProvider();

        assertEquals("The contact name cannot be empty", display.getLastError(),
                "Registration should fail when contact name is empty");
    }

    @Test
    void emptyEmail() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("");

        controller.registerEntertainmentProvider();

        assertEquals("The email cannot be empty", display.getLastError(),
                "Registration should fail when email is empty");
    }

    @Test
    void emptyPassword() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("");

        controller.registerEntertainmentProvider();

        assertEquals("The Password cannot be empty.", display.getLastError(),
                "Registration should fail when password is empty");
    }

    @Test
    void emptyDescription() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("");

        controller.registerEntertainmentProvider();

        assertEquals("the description cannot be empty.", display.getLastError(),
                "Registration should fail when description is empty");
    }

    @Test
    void duplicateOrgNameAndBusinessNumber() {
        // Register first EP
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        // Attempt duplicate with same org name and business number
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("Jane Doe");
        display.addInput("jane@edinfest.com");
        display.addInput("anotherPass456");
        display.addInput("A different description");
        controller.registerEntertainmentProvider();

        assertEquals(
                "An entertainment provider with the same organisation name and business number already exists",
                display.getLastError(),
                "Registration should fail when org name and business number match an existing EP");
    }

    @Test
    void duplicateEmail() {
        // Register first EP
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        // Attempt with same email but different org details
        display.addInput("Glasgow Arts Ltd");
        display.addInput("0987654321");
        display.addInput("Jane Doe");
        display.addInput("john@edinfest.com");
        display.addInput("anotherPass456");
        display.addInput("Another org");
        controller.registerEntertainmentProvider();

        assertEquals(
                "An entertainment provider with the same organisation name and business number already exists",
                display.getLastError(),
                "Registration should fail when email matches an existing EP");
    }

    @Test
    void sameOrgNameDifferentBusinessNumber() {
        // Register first EP
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        // Same org name but different business number and email
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("0987654321");
        display.addInput("Jane Doe");
        display.addInput("jane@edinfest.com");
        display.addInput("anotherPass456");
        display.addInput("A franchise branch");
        controller.registerEntertainmentProvider();

        assertEquals("Edinburgh Festivals Ltd has been successfully registered.",
                display.getLastSuccess(),
                "Registration should succeed when org name matches but business number differs");
    }

    @Test
    void duplicateOrgNameCaseInsensitive() {
        // Register first EP
        display.addInput("Edinburgh Festivals");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        // Same org name in different case + same business number
        display.addInput("EDINBURGH FESTIVALS");
        display.addInput("1234567890");
        display.addInput("Jane Doe");
        display.addInput("jane@edinfest.com");
        display.addInput("anotherPass456");
        display.addInput("Another description");
        controller.registerEntertainmentProvider();

        assertEquals(
                "An entertainment provider with the same organisation name and business number already exists",
                display.getLastError(),
                "Duplicate check should be case-insensitive for org name");
    }

    @Test
    void invalidBusinessNumber() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("12345");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");

        controller.registerEntertainmentProvider();

        assertEquals("We could not verify the business number, registration failed.",
                display.getLastError(),
                "Registration should fail when business number fails verification");
    }

    @Test
    void verificationServiceRejectsAll() {
        controller = new UserController(display, new RejectingVerificationService());

        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");

        controller.registerEntertainmentProvider();

        assertEquals("We could not verify the business number, registration failed.",
                display.getLastError(),
                "Registration should fail when verification service rejects the number");
    }

    @Test
    void businessNumberExactly10Chars() {
        display.addInput("Boundary Test Org");
        display.addInput("ABCDEFGHIJ");
        display.addInput("Alice");
        display.addInput("alice@boundary.com");
        display.addInput("password");
        display.addInput("Testing boundary");

        controller.registerEntertainmentProvider();

        assertEquals("Boundary Test Org has been successfully registered.",
                display.getLastSuccess(),
                "Business number of exactly 10 characters should pass verification");
    }

    @Test
    void businessNumber9Chars() {
        display.addInput("Boundary Test Org");
        display.addInput("ABCDEFGHI");
        display.addInput("Alice");
        display.addInput("alice@boundary.com");
        display.addInput("password");
        display.addInput("Testing boundary");

        controller.registerEntertainmentProvider();

        assertEquals("We could not verify the business number, registration failed.",
                display.getLastError(),
                "Business number of 9 characters should fail verification");
    }

    @Test
    void businessNumber11Chars() {
        display.addInput("Boundary Test Org");
        display.addInput("ABCDEFGHIJK");
        display.addInput("Alice");
        display.addInput("alice@boundary.com");
        display.addInput("password");
        display.addInput("Testing boundary");

        controller.registerEntertainmentProvider();

        assertEquals("We could not verify the business number, registration failed.",
                display.getLastError(),
                "Business number of 11 characters should fail verification");
    }

    @Test
    void whileLoggedIn() {
        // Register and log in
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        controller.login();

        // Attempt registration while logged in
        controller.registerEntertainmentProvider();

        assertEquals("You must log out before registering a new entertainment provider.",
                display.getLastError(),
                "Registration should fail if a user is currently logged in");
    }

    @Test
    void twoDistinctEPsBothSucceed() {
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        assertEquals("Edinburgh Festivals Ltd has been successfully registered.",
                display.getLastSuccess(),
                "First EP registration should succeed");

        display.addInput("Glasgow Arts Ltd");
        display.addInput("0987654321");
        display.addInput("Jane Doe");
        display.addInput("jane@glasgowarts.com");
        display.addInput("anotherPass456");
        display.addInput("We do art shows");
        controller.registerEntertainmentProvider();

        assertEquals("Glasgow Arts Ltd has been successfully registered.",
                display.getLastSuccess(),
                "Second EP with different details should also register successfully");
    }

    @Test
    void twoDistinctEPsBothLogin() {
        // Register two EPs
        display.addInput("Edinburgh Festivals Ltd");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        display.addInput("We organise festivals");
        controller.registerEntertainmentProvider();

        display.addInput("Glasgow Arts Ltd");
        display.addInput("0987654321");
        display.addInput("Jane Doe");
        display.addInput("jane@glasgowarts.com");
        display.addInput("anotherPass456");
        display.addInput("We do art shows");
        controller.registerEntertainmentProvider();

        // Login as first EP
        display.addInput("john@edinfest.com");
        display.addInput("securePass123");
        controller.login();
        assertEquals("Login successful", display.getLastSuccess(),
                "First registered EP should be able to log in");

        controller.logout();

        // Login as second EP
        display.addInput("jane@glasgowarts.com");
        display.addInput("anotherPass456");
        controller.login();
        assertEquals("Login successful", display.getLastSuccess(),
                "Second registered EP should be able to log in");
    }
}
