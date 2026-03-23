package View;

import java.util.Collection;

/**
 * Interface for the user interface of the events app
 *
 * Defines methods for getting user input and displaying
 * various types of output including success/error messages,
 * performance listings, and booking records.
 */

public interface View {
    /**
     * method to prompt user and then get their response.
     *
     * @param inputPrompt the prompt message to display
     * @return the return of the user's input
     */
    String getInput(String inputPrompt);

    /**
     * Displays a success message to the user.
     *
     * @param successMessage the success message to be sent ot display.
     */
    void displaySuccess(String successMessage);

    /**
     * Displays an error message to the user.
     *
     * @param errorMessage the error message to be sent to display
     */
    void displayError(String errorMessage);

    /**
     * Displays the performance list summaries.
     *
     * @param listOfPerformanceInfo
     */
    void displayListofPerformances(Collection<String> listOfPerformanceInfo);

    /**
     * Displays the information on each specific performance.
     *
     * @param performanceInfo
     */
    void displaySpecificPerformance(String performanceInfo);

    /**
     * The method to display the booking method to the user.
     *
     * @param bookingRecord
     */
    void displayBookingRecord(String bookingRecord);
}
