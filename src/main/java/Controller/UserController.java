package Controller;
import User.User;
import View.View;

import java.util.Collection;

public class UserController extends Controller{
    private static final String PREREGISTERED_USERS_FILE_PATH = "users.txt";
    private static final String PREREGISTERED_ADMIN_FILE_PATH = "admin.txt";
    private static Collection<User> users;

    public UserController(View view, Collection<User> users) {
        super(view);
        this.users = users;
    }

    public static void login () {
        String email = view.getInput("Enter email: ");
        String password = view.getInput("Enter password: ");

        for (User x : users) {
            if (x.email.equals(email) && x.password.equals(password)) {
                currentUser = x;
                view.displaySuccess("Login successful");
                return;
            }
        }
        view.displayError("Invalid email or password");
    }

    public static void logout () {
        currentUser = null;
        view.displaySuccess("Logged out successfully");
    }
}
