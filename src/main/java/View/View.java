package View;

import java.util.Collection;

public interface View {
    String getInput(String inputPrompt);
    void displaySuccess(String successMessage);
    void displayError(String errorMessage);
    void displayListofPerformances(Collection<String> listOfPerformanceInfo);
    void displaySpecificPerformance(String performanceInfo);
    void displayBookingRecord(String bookingRecord);
}