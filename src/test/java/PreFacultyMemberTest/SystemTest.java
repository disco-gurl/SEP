package PreFacultyMemberTest;

import PreRegisterFaculty.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import View.View;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTest {
    private static Collection<User> loadFacultyMembers() {
        RegistrationUtility utility = new RegistrationUtility("src/resources/mock_faculty.txt");
        Collection<FacultyMember> facultyMembers = utility.registerFaculty();
        Collection<User> users = new ArrayList<>(facultyMembers);
        return users;
    }

    static class fakeView implements View{
        private Queue<String> inputs = new LinkedList<>();
        private String Success;
        private String Error;

        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return Success; }
        public String getLastError() { return Error; }

        @Override
        public String getInput(String inputPrompt) {
            return "";
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

    @Test //check if the password changing works
    void passwordChange() {
        fakeView display = new fakeView();
        UserController controller = new UserController(display, loadFacultyMembers());

        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        display.addInput("yes");
        display.addInput("new");
        controller.login();

        assertEquals("Password changed", display.getLastSuccess(),
                "Change success for password");
    }

    @Test //log in for the first time
    void firstLogin() {
        fakeView display = new fakeView();
        UserController controller = new UserController(display, loadFacultyMembers());

        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        display.addInput("no");
        controller.login();

        assertEquals("Login successful", display.getLastSuccess(),
                "First Login");
    }

    @Test
    void secondLogin() {
        fakeView display = new fakeView();
        UserController controller = new UserController(display, loadFacultyMembers());

        // first
        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        display.addInput("no");
        controller.login();
        controller.logout();

        // second
        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        controller.login();

        assertEquals("Login successful.", display.getLastSuccess(),
                "No password change");
    }

    @Test //test for the wrong password
    void wrongPassword() {
        fakeView display = new fakeView();
        UserController controller = new UserController(display, loadFacultyMembers());

        display.addInput("tracey@ed.ac.uk");
        display.addInput("wrong");
        controller.login();

        assertNotNull(display.getLastError(),
                "Wrong Password");
        assertTrue(display.getLastError().contains("attempts 1"),
                "Increase log in");
    }

    @Test
    void wrongPasswordAgain() {
        fakeView display = new fakeView();
        UserController controller = new UserController(display, loadFacultyMembers());

        display.addInput("tracey@ed.ac.uk");
        display.addInput("wrong");
        controller.login();

        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        controller.login();

        assertNotNull(display.getLastError(),
                "Wrong Password");
        assertTrue(display.getLastError().contains("attempts 2"),
                "Increase log in");
    }

    @Test
    void logout() {
        fakeView display = new fakeView();
        UserController controller = new UserController(display, loadFacultyMembers());

        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        display.addInput("no");
        controller.login();
        controller.logout();

        assertEquals("Logged out.", display.getLastSuccess(),
                "Successful log out");
    }

    @Test //check with random email
    void loginRandomEmail() {
        fakeView display = new fakeView();
        UserController controller = new UserController(display, loadFacultyMembers());

        display.addInput("random@ed.ac.uk");
        display.addInput("password");
        controller.login();

        assertEquals("Invalid email or password.", display.getLastError(),
                "Error");
    }

    @Test
    void loginAttemptsResetAfterSuccessfulLogin() {
        fakeView display = new fakeView();
        Collection<User> users = loadFacultyMembers();
        UserController controller = new UserController(display, users);

        display.addInput("tracey@ed.ac.uk");
        display.addInput("wrong");
        controller.login();

        display.addInput("tracey@ed.ac.uk");
        display.addInput("group29");
        display.addInput("no");
        controller.login();

        FacultyMember f = null;
        for (User x : users) {
            if (x.getEmail().equals("tracey@ed.ac.uk")) {
                f = (FacultyMember) x;
            }
        }
        assertEquals(0, f.getLoginAttempt(),
                "Login Success");
    }
}
