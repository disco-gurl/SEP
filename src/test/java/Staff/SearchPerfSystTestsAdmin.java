package Staff;

import Controller.EventPerformanceController;
import Controller.UserController;
import External.MockVerificationService;
import User.AdminStaff;
import User.EntertainmentProvider;
import View.View;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

public class SearchPerfSystTestsAdmin {

    private View mockView;
    private UserController userController;
    private EventPerformanceController eventPerformanceController;

    private AdminStaff admin;
    private EntertainmentProvider ep;

    @BeforeEach
    void setUp() {
        mockView = mock(View.class);
        userController = new UserController(mockView, new MockVerificationService());
        eventPerformanceController = new EventPerformanceController(mockView);

        userController.logout();

        admin = new AdminStaff("admin@uni.ac.uk", "adminpass");
        ep = new EntertainmentProvider(
                "ep@shows.com", "pass456", "Edinburgh Concert Company",
                "1234567890", "Libby Goode", "Concerts in Edinburgh");

        userController.addUser(admin);
        userController.addUser(ep);
    }

    // helper methods

    private void loginAs(String email, String password) {
        when(mockView.getInput("Enter email: ")).thenReturn(email);
        when(mockView.getInput("Enter password: ")).thenReturn(password);
        userController.login();
    }

    // creates a ticketed Music event with one performance on 2027-08-05
    private void createMusicEvent() {
        loginAs("ep@shows.com", "pass456");

        when(mockView.getInput("Enter event title: ")).thenReturn("Admin Search Concert");
        when(mockView.getInput("Enter event type (Music, Theatre, Dance, Movie, Sports): "))
                .thenReturn("Music");
        when(mockView.getInput("Is the event ticketed? ")).thenReturn("yes");
        when(mockView.getInput("Enter performance start date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-08-05 19:00");
        when(mockView.getInput("Enter performance end date and time (yyyy-MM-dd HH:mm): "))
                .thenReturn("2027-08-05 21:00");
        when(mockView.getInput("Enter performer names (comma-separated): "))
                .thenReturn("Solo Artist");
        when(mockView.getInput("Enter venue address: ")).thenReturn("The Venue");
        when(mockView.getInput("Enter venue capacity: ")).thenReturn("800");
        when(mockView.getInput("Is the venue outdoors? ")).thenReturn("no");
        when(mockView.getInput("Does the venue allow smoking? ")).thenReturn("no");
        when(mockView.getInput("Enter total number of tickets: ")).thenReturn("300");
        when(mockView.getInput("Enter ticket price: ")).thenReturn("18.00");
        when(mockView.getInput("Would you like to add another performance? ")).thenReturn("no");

        eventPerformanceController.createEvent();
        userController.logout();
    }

    // tests

    @Test
    void testGuestRejectedAdmin() {
        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("You must be logged in to search for performances");
    }

    @Test
    void testInvalidDateFormatAdmin() {
        loginAs("admin@uni.ac.uk", "adminpass");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("20270805")       // no dashes — wrong format
                .thenReturn("2030-01-01");    // valid, no results

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("Invalid date format. Please use yyyy-mm-dd");
    }

    @Test
    void testNoPerformancesOnDateAdmin() {
        loginAs("admin@uni.ac.uk", "adminpass");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2030-01-01");

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("No performances found on 2030-01-01");
    }

    @Test
    void testFindPerformanceCorrectDateAdmin() {
        createMusicEvent();
        loginAs("admin@uni.ac.uk", "adminpass");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-08-05");

        eventPerformanceController.searchforPerformances();

        verify(mockView, atLeastOnce()).displaySuccess(anyString());
        verify(mockView, never()).displayError("No performances found on 2027-08-05");
    }
    @Test
    void testSearchOnWrongDateAdmin() {
        createMusicEvent();
        loginAs("admin@uni.ac.uk", "adminpass");
        clearInvocations(mockView);

        when(mockView.getInput("Enter a date to search for performances (yyyy-mm-dd): "))
                .thenReturn("2027-08-04");

        eventPerformanceController.searchforPerformances();

        verify(mockView).displayError("No performances found on 2027-08-04");
    }

}
