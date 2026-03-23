package View;

import java.util.Collection;
import java.util.Scanner;

/**
 * The text implementation of the View interface
 */
public class TextUserInterface implements View {

    /**
     * Constructs the interface with a provided scanner.
     *
     * @param scanner the provided scanner.
     */
    public TextUserInterface(Scanner scanner){
        this.scanner = scanner;
    }

    /**
     * Constructs the interface using System.in.
     */
    public TextUserInterface(){
        this(new Scanner(System.in));
    }

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
    public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {
        if (listOfPerformanceInfo == null || listOfPerformanceInfo.isEmpty()) {
            System.out.println("There are no performances to display");
            return;
        }
        System.out.println("Performances");
        int index = 1;
        for (String info : listOfPerformanceInfo) {
            System.out.println(index + ". " + info);
            index++;
        }

    }

    @Override
    public void displaySpecificPerformance(String performanceInfo) {
        System.out.println("Performance Details");
        System.out.println(performanceInfo);
    }

    @Override
    public void displayBookingRecord(String bookingRecord) {
        System.out.println("Booking Record");
        System.out.println(bookingRecord);
    }
}


