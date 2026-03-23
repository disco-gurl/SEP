package View;

import java.util.Collection;
import java.util.Scanner;

public class TextUserInterface implements View {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public String getInput(String inputPrompt) {
        System.out.print(inputPrompt);
        return scanner.nextLine();
    }

    @Override
    public void displaySuccess(String successMessage) {
        System.out.println("Success: " + successMessage);
    }

    @Override
    public void displayError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }

    @Override
    public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {
        for (String performance : listOfPerformanceInfo) {
            System.out.println(performance);
        }
    }

    @Override
    public void displaySpecificPerformance(String performanceInfo) {
        System.out.println(performanceInfo);
    }

    @Override
    public void displayBookingRecord(String bookingRecord) {
        System.out.println(bookingRecord);
    }
}