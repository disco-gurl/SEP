package Controller;

import Performance.Performance;
import View.View;

import java.util.Collection;

public class MenuController extends Controller {
    private UserController userController;
    private EventPerformanceController eventPerformanceController;
    private BookingController bookingController;
    private Collection<Performance> performances;

    public MenuController(View view) {
        super(view);
    }
}
