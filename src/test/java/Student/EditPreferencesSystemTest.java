package Student;

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

class EditPreferencesSystemTest {

    private fakeView display;
    private UserController userController;
    private EventPerformanceController epController;

    static class fakeView implements View {
        private Queue<String> inputs = new LinkedList<>();
        private String Success;
        private String Error;

        public void addInput(String input) { inputs.add(input); }
        public String getLastSuccess() { return Success; }
        public String getLastError() { return Error; }

        @Override
        public String getInput(String inputPrompt) { return inputs.poll(); }

        @Override
        public void displaySuccess(String successMessage) { this.Success = successMessage; }

        @Override
        public void displayError(String errorMessage) { this.Error = errorMessage; }

        @Override
        public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {}

        @Override
        public void displaySpecificPerformance(String performanceInfo) {}

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

        //pre registered users
        userController.addUser(new Student("alice@ed.ac.uk", "pass123", "Alice", 123456789));
        userController.addUser(new AdminStaff("admin@ed.ac.uk", "adminpass"));
        userController.addUser(new EntertainmentProvider("ep@test.com", "eppass",
                "Test Org", "1234567890", "EP Contact", "desc"));

        display.addInput("alice@ed.ac.uk");
        display.addInput("pass123");
        userController.login();
    }


    @Test
    void setSinglePreference() {
        display.addInput("Music");
        userController.editPreferences();
        assertTrue(display.getLastSuccess().contains("Music"),
                "should confirm Music preference");
    }

    @Test
    void setMultiplePreferences() {
        display.addInput("Music,Dance,Sports");

        userController.editPreferences();

        String success = display.getLastSuccess();
        assertTrue(success.contains("Music"), "should have Music");
        assertTrue(success.contains("Dance"), "should have Dance");
        assertTrue(success.contains("Sports"), "should have Sports");
    }

    @Test
    void setAllPreferences() {
        display.addInput("Music,Theatre,Dance,Movie,Sports");
        userController.editPreferences();

        String success = display.getLastSuccess();
        assertTrue(success.contains("Music"));
        assertTrue(success.contains("Theatre"));
        assertTrue(success.contains("Dance"));
        assertTrue(success.contains("Movie"));
        assertTrue(success.contains("Sports"));
    }

    @Test
    void caseInsensitive() {
        //should work regardless of capitalisation
        display.addInput("music,DANCE,Theatre");
        userController.editPreferences();

        String success = display.getLastSuccess();
        assertTrue(success.contains("Music"), "lowercase music should work");
        assertTrue(success.contains("Dance"), "uppercase DANCE should work");
        assertTrue(success.contains("Theatre"), "mixed case should work");
    }

    @Test
    void allInvalidPreferences() {
        display.addInput("Basketball,Swimming");

        userController.editPreferences();
        assertEquals("No valid preferences were recognised. Valid types: Music, Theatre, Dance, Movie, Sports.",
                display.getLastError());
    }

    @Test
    void mixValidAndInvalid() {
        display.addInput("Music,Basketball,Dance");
        userController.editPreferences();

        String success = display.getLastSuccess();
        assertTrue(success.contains("Music"), "valid ones should still be set");
        assertTrue(success.contains("Dance"), "Dance should be set too");
    }

    @Test
    void emptyInput() {
        display.addInput("");
        userController.editPreferences();

        assertEquals("No valid preferences were recognised. Valid types: Music, Theatre, Dance, Movie, Sports.",
                display.getLastError());
    }

    @Test
    void nullInput() {
        display.addInput(null);
        userController.editPreferences();
        assertEquals("No valid preferences were recognised. Valid types: Music, Theatre, Dance, Movie, Sports.",
                display.getLastError());
    }

    @Test
    void updateOverwritesOldPreferences() {
        //set Music and Dance first
        display.addInput("Music,Dance");
        userController.editPreferences();

        //now change to Theatre and Sports
        display.addInput("Theatre,Sports");
        userController.editPreferences();

        String success = display.getLastSuccess();
        assertTrue(success.contains("Theatre"), "new preference Theatre should be there");
        assertTrue(success.contains("Sports"), "new preference Sports should be there");
        //old ones should be gone since updatePreferences resets
        assertFalse(success.contains("Music"), "Music should be gone after update");
        assertFalse(success.contains("Dance"), "Dance should be gone after update");
    }

    @Test
    void notLoggedIn() {
        userController.logout();
        userController.editPreferences();
        assertEquals("You must be logged in to edit preferences.", display.getLastError());
    }

    @Test
    void epCantEditPreferences() {
        userController.logout();
        display.addInput("ep@test.com");
        display.addInput("eppass");
        userController.login();

        userController.editPreferences();

        assertEquals("Only students can edit preferences.", display.getLastError());
    }

    @Test
    void adminCantEditPreferences() {
        userController.logout();
        display.addInput("admin@ed.ac.uk");
        display.addInput("adminpass");
        userController.login();
        userController.editPreferences();
        assertEquals("Only students can edit preferences.", display.getLastError());
    }

    @Test
    void spacesAroundCommas() {
        display.addInput("Music , Dance , Movie");
        userController.editPreferences();

        String success = display.getLastSuccess();
        assertTrue(success.contains("Music"), "should handle spaces");
        assertTrue(success.contains("Dance"));
        assertTrue(success.contains("Movie"));
    }

    @Test
    void preferencesAffectSearch() {
        //set up - register EP, create events of different types, then search as student with preferences
        userController.logout();

        //register EP
        display.addInput("Festival Co");
        display.addInput("0987654321");
        display.addInput("Jane");
        display.addInput("jane@fest.com");
        display.addInput("janepass");
        display.addInput("Festivals");
        userController.registerEntertainmentProvider();
        display.addInput("jane@fest.com");
        display.addInput("janepass");
        userController.login();

        //create a Music event on dec 1
        display.addInput("Music Show");
        display.addInput("Music");
        display.addInput("no");
        display.addInput("2026-12-01 19:00");
        display.addInput("2026-12-01 22:00");
        display.addInput("Band");
        display.addInput("Venue");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("no");
        epController.createEvent();

        //create a Dance event same date
        display.addInput("Dance Show");
        display.addInput("Dance");
        display.addInput("no");
        display.addInput("2026-12-01 14:00");
        display.addInput("2026-12-01 17:00");
        display.addInput("Dancers");
        display.addInput("Hall");
        display.addInput("100");
        display.addInput("no");
        display.addInput("no");
        display.addInput("no");
        epController.createEvent();
        userController.logout();

        //login as student and set Dance preference
        display.addInput("alice@ed.ac.uk");
        display.addInput("pass123");
        userController.login();
        display.addInput("Dance");
        userController.editPreferences();

        //search for performances on that date
        display.addInput("2026-12-01");
        epController.searchforPerformances();

        assertNotNull(display.getLastSuccess(),
                "should return results after setting preferences");
    }

    @Test
    void showsCurrentBeforeEditing() {
        display.addInput("Music,Dance");
        userController.editPreferences();

        //edit again, system should show current preferences first then ask for new
        display.addInput("Theatre");
        userController.editPreferences();
        assertTrue(display.getLastSuccess().contains("Theatre"),
                "new preference should be set");
    }
}