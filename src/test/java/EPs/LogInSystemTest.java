package EPs;

import Controller.UserController;
import External.VerificationService;
import User.*;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class LogInSystemTest {
    private LogInSystemTest.fakeView display;
    private UserController controller;

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
        public void displayBookingRecord(String bookingRecord) {

        }

    }

    static class fakeVerificationService implements VerificationService {
        @Override
        public boolean verifyEntertainmentProvider(String businessRegistrationNumber) {
            return true;
        }
    }

    @BeforeEach
    void setUp() {
        display = new LogInSystemTest.fakeView();
        controller = new UserController(display, new fakeVerificationService());
        controller.addUser(new EntertainmentProvider("uniofedi@ed.ac.uk", "group29",
                "University of Edinburgh", "123er", "Edinburgh", "university"));
    }


    @Test
    void Succesfullogin() {
        display.addInput("uniofedi@ed.ac.uk");
        display.addInput("group29");

        controller.login();
        assertEquals("Login successful", display.getLastSuccess(),
                "Success");
    }

    @Test
    void wrongpassword() {
        display.addInput("uniofedi@ed.ac.uk");
        display.addInput("wrongpassword");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong password");
    }

    @Test
    void wrongemail() {
        display.addInput("wrong@ed.ac.uk");
        display.addInput("group29");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong email");
    }

    @Test
    void bothwrong() {
        display.addInput("wrong@ed.ac.uk");
        display.addInput("wrongpassword");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong email");
    }

    @Test
    void emptypassword() {
        display.addInput("uniofedi@ed.ac.uk");
        display.addInput("");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong email");
    }

    @Test
    void emptyemail() {
        display.addInput("");
        display.addInput("group29");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong email");
    }

    @Test
    void bothempty() {
        display.addInput("");
        display.addInput("");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong password");
    }

    @Test
    void emailuppercase() {
        display.addInput("UNIOFEDI@ed.ac.uk");
        display.addInput("group29");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong password");
    }

    @Test
    void passworduppercase() {
        display.addInput("uniofedi@ed.ac.uk");
        display.addInput("GROUP29");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong password");
    }

    @Test
    void emailspaces() {
        display.addInput(" uniofedi@ed.ac.uk ");
        display.addInput("group29");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong password");
    }

    @Test
    void passwordspaces() {
        display.addInput("uniofedi@ed.ac.uk");
        display.addInput(" group29 ");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong password");
    }

    @Test
    void bothspaces() {
        display.addInput(" uniofedi@ed.ac.uk ");
        display.addInput(" group29 ");

        controller.login();
        assertEquals("Invalid email or password", display.getLastError(),
                "wrong password");
    }
}