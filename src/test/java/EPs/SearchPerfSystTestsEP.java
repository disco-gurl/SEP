package EPs;

import Controller.EventPerformanceController;
import Controller.UserController;
import External.MockVerificationService;
import User.EntertainmentProvider;
import View.View;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SearchPerfSystTestsEP {

    private View mockView;
    private UserController userController;
    private EventPerformanceController eventPerformanceController;

    private EntertainmentProvider ep;

    @BeforeEach
    void setUp() {
        mockView = mock(View.class);
        userController = new UserController(mockView, new MockVerificationService());
        eventPerformanceController = new EventPerformanceController(mockView);

        userController.logout();

        ep = new EntertainmentProvider(
                "ep@shows.com", "pass456", "Edinburgh Concert Company",
                "1234567890", "Libby Goode", "Concerts in Edinburgh");
        userController.addUser(ep);
    }

    // helpers

    private void loginAs(String email, String password) {
        when(mockView.getInput("Enter email: ")).thenReturn(email);
        when(mockView.getInput("Enter password: ")).thenReturn(password);
        userController.login();
    }

    private void createMusicEvent() {
        loginAs("ep@shows.com", "pass456");

        when(mockView.getInput("Enter event title: ")).thenReturn("The Fringe");
        when(mockView.getInput("Enter event type (Music, Theatre, Dance, Movie, Sports): "))
                .thenReturn("Music");
        when(mockView.getInput("Is the event ticketed? ")).thenReturn("yes");
        when(mockView.getInput("Enter performance start date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-06-15 20:00");
        when(mockView.getInput("Enter performance end date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-06-15 22:00");
        when(mockView.getInput("Enter performer names (comma-separated): "))
                .thenReturn("The Edinburgers");
        when(mockView.getInput("Enter venue address: ")).thenReturn("Meadows");
        when(mockView.getInput("Enter venue capacity: ")).thenReturn("500");
        when(mockView.getInput("Is the venue outdoors? ")).thenReturn("no");
        when(mockView.getInput("Does the venue allow smoking? ")).thenReturn("no");
        when(mockView.getInput("Enter total number of tickets: ")).thenReturn("200");
        when(mockView.getInput("Enter ticket price: ")).thenReturn("20.00");
        when(mockView.getInput("Would you like to add another performance? ")).thenReturn("no");

        eventPerformanceController.createEvent();
        userController.logout();
    }

    // tests

    @Test
    void testGuestRejectedEP() {
        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("You must be logged in to search for performances");
    }

    @Test
    void testInvalidDateFormatEP() {
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("June 15 2027")  // wrong format
                .thenReturn("2030-01-01");   // valid, no results

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("Invalid date format. Please use yyyy-mm-dd");
    }

    @Test
    void testNoPerformancesOnDateEP() {
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2030-01-01");

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("No performances found on 2030-01-01");
    }

    @Test
    void testFindPerformanceCorrectDateEP() {
        createMusicEvent();
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-06-15");

        eventPerformanceController.searchforPerformances();

        verify(mockView, atLeastOnce()).displaySuccess(anyString());
        verify(mockView, never()).displayError("No performances found on 2027-06-15");
    }

    @Test
    void testSearchOnWrongDateEP() {
        createMusicEvent();
        loginAs("ep@shows.com", "pass456");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-06-14");

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("No performances found on 2027-06-14");
    }

}
