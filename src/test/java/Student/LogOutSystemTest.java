package Student;

import Controller.UserController;
import External.VerificationService;
import User.*;
import View.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class LogOutSystemTest {
    private fakeView display;
    private UserController controller;

    static class fakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String Success;
        private String Error;


        public void addInput(String input) {
            inputs.add(input);
        }

        public String getLastSuccess() {
            return Success;
        }

        public String getLastError() {
            return Error;
        }

        @Override
        public String getInput(String inputPrompt) {
            return inputs.poll();
        }

        @Override
        public void displaySuccess(String successMessage) {
            this.Success = successMessage;
        }

        @Override
        public void displayError(String errorMessage) {
            this.Error = errorMessage;
        }

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
        display = new fakeView();
        controller = new UserController(display, new fakeVerificationService());
        controller.addUser(new Student("tracey@ed.ac.uk", "group29", "Tracey", 77));
    }


    @Test
    void Succesfullogin() {
        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");

        controller.login();
        assertEquals("Login successful", display.getLastSuccess(),
                "Success");
    }

    @Test
    void logoutthenin() {
        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        controller.login();

        controller.logout();

        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        controller.login();
        assertEquals("Login successful", display.getLastSuccess());
    }

    @Test
    void nologin() {
        controller.logout();
        assertEquals("Logged out successfully", display.getLastSuccess());
    }
}