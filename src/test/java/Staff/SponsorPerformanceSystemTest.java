package Staff;

import Controller.EventPerformanceController;
import Controller.UserController;
import External.VerificationService;
import User.AdminStaff;
import User.EntertainmentProvider;
import User.Student;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class SponsorPerformanceSystemTest {

    private fakeView display;
    private UserController userController;
    private EventPerformanceController epController;

    static class fakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String Success;
        private String Error;
        private String performanceDisplay;

        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return Success; }
        public String getLastError() { return Error; }
        public String getPerformanceDisplay() { return performanceDisplay; }

        @Override
        public String getInput(String inputPrompt) { return inputs.poll(); }

        @Override
        public void displaySuccess(String successMessage) { this.Success = successMessage; }

        @Override
        public void displayError(String errorMessage) { this.Error = errorMessage; }

        @Override
        public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {}

        @Override
        public void displaySpecificPerformance(String performanceInfo) {
            this.performanceDisplay = performanceInfo;
        }

        @Override
        public void displayBookingRecord(String bookingRecord) {}
    }

    static class fakeVerificationService implements VerificationService {
        @Override
        public boolean verifyEntertainmentProvider(String businessRegistrationNumber) {
            return businessRegistrationNumber != null
                    && businessRegistrationNumber.length() == 10;
        }
    }

    @BeforeEach
    void setUp() {
        display = new fakeView();
        userController = new UserController(display, new fakeVerificationService());
        epController = new EventPerformanceController(display);

        userController.addUser(new Student("student@ed.ac.uk", "studentpass", "Alice", 123456789));
        userController.addUser(new AdminStaff("admin@ed.ac.uk", "adminpass"));

        //register EP and create a ticketed event with one performance
        display.addInput("Edinburgh Festivals");
        display.addInput("1234567890");
        display.addInput("John Smith");
        display.addInput("john@edinfest.com");
        display.addInput("eppass");
        display.addInput("Festival organiser");
        userController.registerEntertainmentProvider();
        display.addInput("john@edinfest.com");
        display.addInput("eppass");
        userController.login();

        //create ticketed event, ticket price is 25
        display.addInput("Jazz Night");
        display.addInput("Music");
        display.addInput("yes");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band A");
        display.addInput("Usher Hall");
        display.addInput("500");
        display.addInput("no");
        display.addInput("no");
        display.addInput("200");
        display.addInput("25.00");
        display.addInput("no");
        epController.createEvent();

        userController.logout();
    }

    @Test
    void successfulSponsorship() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();

        display.addInput("1");       //performance id
        display.addInput("10.00");   //amount
        epController.sponsorPerformance();

        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void sponsorFullPrice() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        display.addInput("1");
        display.addInput("25.00");   //full ticket price

        epController.sponsorPerformance();
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void notLoggedIn() {
        epController.sponsorPerformance();
        assertEquals("You must be logged in to sponsor a performance.", display.getLastError());
    }

    @Test
    void studentCantSponsor() {
        display.addInput("student@ed.ac.uk");
        display.addInput("studentpass");
        userController.login();
        epController.sponsorPerformance();
        assertEquals("Only admin staff can sponsor performances.", display.getLastError());
    }

    @Test
    void epCantSponsor() {
        display.addInput("john@edinfest.com");
        display.addInput("eppass");
        userController.login();

        epController.sponsorPerformance();

        assertEquals("Only admin staff can sponsor performances.", display.getLastError());
    }

    @Test
    void invalidPerformanceID() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();

        display.addInput("abc");     //not a number
        //after error, give valid input
        display.addInput("1");
        display.addInput("10.00");
        epController.sponsorPerformance();

        assertEquals("Invalid performance ID format.", display.getLastError());
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void nonExistentPerformance() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        display.addInput("999");
        //retry with valid
        display.addInput("1");
        display.addInput("10.00");

        epController.sponsorPerformance();
        assertEquals("Performance with given number does not exist.", display.getLastError());
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void negativeAmount() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();

        display.addInput("1");
        display.addInput("-5.00");
        //retry
        display.addInput("1");
        display.addInput("10.00");

        epController.sponsorPerformance();

        assertEquals("The amount provided is invalid.", display.getLastError());
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void amountMoreThanTicketPrice() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        display.addInput("1");
        display.addInput("30.00");   //more than the 25 ticket price
        //retry with valid amount
        display.addInput("1");
        display.addInput("10.00");
        epController.sponsorPerformance();

        assertEquals("The amount provided is invalid.", display.getLastError());
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void invalidAmountFormat() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        display.addInput("1");
        display.addInput("ten");     //not a number
        //retry
        display.addInput("1");
        display.addInput("10.00");

        epController.sponsorPerformance();

        assertEquals("Invalid amount format.", display.getLastError());
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void cantSponsorNonTicketed() {
        //need to create a non ticketed event first
        display.addInput("john@edinfest.com");
        display.addInput("eppass");
        userController.login();
        display.addInput("Free Show");
        display.addInput("Movie");
        display.addInput("no");
        display.addInput("2026-12-05 18:00");
        display.addInput("2026-12-05 20:00");
        display.addInput("Panel");
        display.addInput("Theatre");
        display.addInput("300");
        display.addInput("no");
        display.addInput("no");
        display.addInput("no");
        epController.createEvent();
        userController.logout();

        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();

        //the non ticketed performance
        display.addInput("2");

        //it asks for amount before checking if ticketed
        display.addInput("5.00");

        //after error give the ticketed one
        display.addInput("1");
        display.addInput("10.00");

        epController.sponsorPerformance();

        assertEquals("The requested performance's event is non ticketed. It cannot be sponsored.",
                display.getLastError());
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }

    @Test
    void sponsorshipReducesPrice() {
        //sponsor then view to check price went down
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        display.addInput("1");
        display.addInput("10.00");
        epController.sponsorPerformance();

        //view the performance
        display.addInput("1");
        epController.viewPerformance();

        String output = display.getPerformanceDisplay();
        assertNotNull(output, "should be able to view it");
        //original was 25, sponsored 10, final should be 15
        assertTrue(output.contains("15.0"),
                "price should be reduced (25 - 10 = 15)");
    }

    @Test
    void sponsorFullPriceMakesFree() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        display.addInput("1");
        display.addInput("25.00");
        epController.sponsorPerformance();

        display.addInput("1");
        epController.viewPerformance();
        String output = display.getPerformanceDisplay();
        assertTrue(output.contains("0.0"), "fully sponsored should be free");
    }

    @Test
    void multipleWrongIDsThenCorrect() {
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();

        display.addInput("999");
        display.addInput("888");
        display.addInput("1");
        display.addInput("10.00");

        epController.sponsorPerformance();
        assertEquals("Performance with given number does not exist.", display.getLastError());
        assertEquals("Sponsorship Successful!", display.getLastSuccess());
    }
}