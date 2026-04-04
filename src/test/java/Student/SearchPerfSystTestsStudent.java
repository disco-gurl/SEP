package Student;

import Controller.EventPerformanceController;
import Controller.UserController;
import External.MockVerificationService;
import User.EntertainmentProvider;
import User.Student;
import User.StudentPreferences;
import View.View;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SearchPerfSystTestsStudent {

    private View mockView;
    private UserController userController;
    private EventPerformanceController eventPerformanceController;

    private Student student;
    private EntertainmentProvider ep;

    @BeforeEach
    void setUp() {
        mockView = mock(View.class);
        userController = new UserController(mockView, new MockVerificationService());
        eventPerformanceController = new EventPerformanceController(mockView);

        userController.logout();

        student = new Student("student@uni.ac.uk", "password123", "Libby Goode",
                123456789);
        ep = new EntertainmentProvider(
                "ep@shows.com", "pass456", "Edinburgh Concert Company",
                "1234567890", "Jane Goodall", "Concerts in Edinburgh");

        userController.addUser(student);
        userController.addUser(ep);
    }

    // helpers

    private void loginAs(String email, String password) {
        when(mockView.getInput("Enter email: ")).thenReturn(email);
        when(mockView.getInput("Enter password: ")).thenReturn(password);
        userController.login();
    }

    // creates a single ticketed Music event with one performance on 2027-06-15

    private void createMusicEvent() {
        loginAs("ep@shows.com", "pass456");

        when(mockView.getInput("Enter event title: ")).thenReturn("Test Concert");
        when(mockView.getInput("Enter event type (Music, Theatre, Dance, Movie, Sports): "))
                .thenReturn("Music");
        when(mockView.getInput("Is the event ticketed? ")).thenReturn("yes");
        when(mockView.getInput("Enter performance start date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-06-15 19:00");
        when(mockView.getInput("Enter performance end date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-06-15 21:00");
        when(mockView.getInput("Enter performer names (comma-separated): "))
                .thenReturn("Artist One");
        when(mockView.getInput("Enter venue address: ")).thenReturn("Edinburgh Arena");
        when(mockView.getInput("Enter venue capacity: ")).thenReturn("1000");
        when(mockView.getInput("Is the venue outdoors? ")).thenReturn("no");
        when(mockView.getInput("Does the venue allow smoking? ")).thenReturn("no");
        when(mockView.getInput("Enter total number of tickets: ")).thenReturn("100");
        when(mockView.getInput("Enter ticket price: ")).thenReturn("25.00");
        when(mockView.getInput("Would you like to add another performance? ")).thenReturn("no");

        eventPerformanceController.createEvent();
        userController.logout();
    }

    // tests

    @Test
    void testGuestRejectedStudent() {
        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("You must be logged in to search for performances");
    }
    @Test
    void testInvalidDateFormatStudent() {
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("15/06/2027")     // wrong format
                .thenReturn("2030-01-01");    // valid format, no results expected

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("Invalid date format. Please use yyyy-mm-dd");
    }
    @Test
    void testNoPerformancesOnDateStudent() {
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2030-01-01");

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("No performances found on 2030-01-01");
    }
    @Test
    void testFindPerformanceCorrectDateStudent() {
        createMusicEvent();
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-06-15");

        eventPerformanceController.searchforPerformances();

        verify(mockView, atLeastOnce()).displaySuccess(anyString());
        verify(mockView, never()).displayError("No performances found on 2027-06-15");
    }
    @Test
    void testSearchOnWrongDateStudent() {
        createMusicEvent();
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-06-14");

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("No performances found on 2027-06-14");
    }
    @Test
    void testStudentMusicPreferences() {
        loginAs("ep@shows.com", "pass456");

        when(mockView.getInput("Enter event title: "))
                .thenReturn("Theatre Show")
                .thenReturn("Music Gig");
        when(mockView.getInput("Enter event type (Music, Theatre, Dance, Movie, Sports): "))
                .thenReturn("Theatre")
                .thenReturn("Music");
        when(mockView.getInput("Is the event ticketed? "))
                .thenReturn("yes").thenReturn("yes");
        when(mockView.getInput("Enter performance start date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-07-20 18:00")
                .thenReturn("2027-07-20 20:30");
        when(mockView.getInput("Enter performance end date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-07-20 20:00")
                .thenReturn("2027-07-20 22:30");
        when(mockView.getInput("Enter performer names (comma-separated): "))
                .thenReturn("Theatre Group").thenReturn("Rock Band");
        when(mockView.getInput("Enter venue address: "))
                .thenReturn("Playhouse").thenReturn("Music Hall");
        when(mockView.getInput("Enter venue capacity: "))
                .thenReturn("500").thenReturn("300");
        when(mockView.getInput("Is the venue outdoors? "))
                .thenReturn("no").thenReturn("no");
        when(mockView.getInput("Does the venue allow smoking? "))
                .thenReturn("no").thenReturn("no");
        when(mockView.getInput("Enter total number of tickets: "))
                .thenReturn("200").thenReturn("100");
        when(mockView.getInput("Enter ticket price: "))
                .thenReturn("15.00").thenReturn("20.00");
        when(mockView.getInput("Would you like to add another performance? "))
                .thenReturn("no").thenReturn("no");

        eventPerformanceController.createEvent(); // Theatre Show (ID 1)
        eventPerformanceController.createEvent(); // Music Gig   (ID 2)
        userController.logout();

        // Give the student a Music preference.
        StudentPreferences prefs = new StudentPreferences(student);
        prefs.updatePreferences("Music");
        student.setStudentPreferences(prefs);

        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-07-20");

        eventPerformanceController.searchforPerformances();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockView, atLeastOnce()).displaySuccess(captor.capture());

        List<String> displayed = captor.getAllValues();
        int musicIndex = -1;
        int theatreIndex = -1;
        for (int i = 0; i < displayed.size(); i++) {
            if (displayed.get(i).contains("Music Gig"))    musicIndex  = i;
            if (displayed.get(i).contains("Theatre Show")) theatreIndex = i;
        }

        assertTrue(musicIndex  != -1, "Music Gig performance should be displayed");
        assertTrue(theatreIndex != -1, "Theatre Show performance should be displayed");
        assertTrue(musicIndex < theatreIndex,
                "Music event (matching preference) should appear before Theatre event");
    }
    @Test
    void testStudentNoPreferences() {
        createMusicEvent();
        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        assertNull(student.getStudentPreferences(),
                "Student should have no preferences for this test");

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-06-15");

        eventPerformanceController.searchforPerformances();

        verify(mockView, atLeastOnce()).displaySuccess(anyString());
        verify(mockView, never()).displayError(anyString());
    }
    @Test
    void testStudentNonMatchingPreferences() {
        createMusicEvent();

        // Student prefers Sports, but only a Music event exists.
        StudentPreferences prefs = new StudentPreferences(student);
        prefs.updatePreferences("Sports");
        student.setStudentPreferences(prefs);

        loginAs("student@uni.ac.uk", "password123");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-06-15");

        eventPerformanceController.searchforPerformances();

        // Music performance is non-matching but should still be shown.
        verify(mockView, atLeastOnce()).displaySuccess(anyString());
        verify(mockView, never()).displayError(anyString());
    }
}
