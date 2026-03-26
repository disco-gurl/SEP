package PreRegisterFaculty;

import View.View;

import java.util.Collection;

public class UserController extends Controller {
    private static final String PREREGISTERED_USERS_FILE_PATH = "users.txt";
    private static final String PREREGISTERED_ADMIN_FILE_PATH = "admin.txt";
    private static Collection<User> users;

    public UserController(View view, Collection<User> users) {
        super(view);
        UserController.users = users;
    }

    public static void login () {
        String email = view.getInput("Enter email: ");
        String password = view.getInput("Enter password: ");

        for (User x : users) {
            if (x.getEmail().equals(email) && x.getPassword().equals(password)) {
                currentUser = x;

                //check if they are a faulty member
                if (x instanceof FacultyMember f) {
                    f.resetAttempts(); //if they are a faculty member then login is 0


                    if (f.isFirstLogin()) { //check for first login
                        view.displaySuccess("Login successful");
                        String change = view.getInput("Do you want to change password?");

                        if (change.equalsIgnoreCase("yes")) { //if it is in any form of yes
                            String newPassword = view.getInput("Password:");
                            f.setPassword(newPassword);
                            view.displaySuccess("Password Change Successful");
                        }
                        else {
                            view.displaySuccess("Login successful");
                        }

                        f.setFirstLogin(false); //it is not the first log in
                    }

                    else {
                        view.displaySuccess("Login successful");
                    }
                }
                else {
                    view.displaySuccess("Login successful");
                }

                return;
            }
        }

        for (User x : users) {
          if (x.getEmail().equals(email) && x instanceof FacultyMember) {
              FacultyMember f = (FacultyMember) x;
              f.increaseLogins(); //increase attempts if wrong password
              view.displayError("Invalid Password, attempts " + f.getLoginAttempt());
              return;
          }
        }

        view.displayError("Invalid email or password");
    }

    public static void logout () {
        currentUser = null;
        view.displaySuccess("Logged out successfully");
    }

    public static Collection<User> getUsers() {
        return users;
    }
}
