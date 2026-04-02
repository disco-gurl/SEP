package Controller;

import External.VerificationService;
import User.EntertainmentProvider;
import User.User;
import View.View;

import User.Student;
import User.StudentPreferences;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Controller for user operations, login, lgout, registration and preferences editing.
 */
public class UserController extends Controller{
    private Collection<User> users;
    private VerificationService verificationService;

    public static final String PREREGISTERED_USERS_FILE_PATH = "preregistered_users.csv";
    public static final String PREREGISTERED_ADMIN_FILE_PATH = "preregistered_admins.csv";

    /**
     * Constructing the UserController using the view and VerificationService given.
     *
     * @param view
     * @param verificationService the service used to verify the business numbers.
     */
    public UserController(View view, VerificationService verificationService) {
        super(view);
        this.users = new ArrayList<>();
        this.verificationService = verificationService;
    }

    public void login() {
        while (true) {
            //loops through until the email and password is correct
            String email = getView().getInput("Enter email: ");
            String password = getView().getInput("Enter password: ");

            for (User x : users) {
                if (x.getEmail().equals(email) && x.checkPassword(password)) {
                    setCurrentUser(x);
                    getView().displaySuccess("Login successful");
                    return;
                }
            }
            getView().displayError("Invalid email or password");
        }
    }

    public void logout() {
        setCurrentUser(null);
        getView().displaySuccess("Logged out successfully");
    }

public void registerEntertainmentProvider() {
        if (getCurrentUser() != null) {
            getView().displayError("You must log out before registering a new entertainment provider.");
            return;
        }

        String orgName = getView().getInput("Enter organisation name: ");
        if (orgName == null || orgName.trim().isEmpty()) {
            getView().displayError("The organisation name cannot be empty");
            return;
        }

        String businessNumber = getView().getInput("Enter business registration number: ");
        if (businessNumber == null || businessNumber.trim().isEmpty()) {
            getView().displayError("The business number cannot be empty");
            return;
        }

        String name = getView().getInput("Enter the main contact name: ");
        if (name == null || name.trim().isEmpty()) {
            getView().displayError("The contact name cannot be empty");
            return;
        }

        String email = getView().getInput("Enter the main contact email:");
        if (email == null || email.trim().isEmpty()) {
            getView().displayError("The email cannot be empty");
            return;
        }

        String password = getView().getInput("Enter your password: ");
        if (password == null || password.trim().isEmpty()) {
            getView().displayError("The Password cannot be empty.");
            return;
        }

        String description = getView().getInput("Enter the organisation description: ");
        if (description == null || description.trim().isEmpty()) {
            getView().displayError("the description cannot be empty.");
            return;
        }

        if (EPAccountAlreadyExists(email, orgName, businessNumber)) {
            getView().displayError("An entertainment provider with the same organisation name and business number already exists");
            return;
        }

        if (!verificationService.verifyEntertainmentProvider(businessNumber)) {
            getView().displayError("We could not verify the business number, registration failed.");
            return;
        }

        EntertainmentProvider newEP = new EntertainmentProvider(email, password, orgName, businessNumber, name, description);
        addUser(newEP);
        getView().displaySuccess(orgName + " has been successfully registered.");

    }


    // Edit preferences use case, lets a student set which event types they prefer.
    //they enter a comma separated string like "Music,Dance,Sports"
    public void editPreferences() {
        if (getCurrentUser() == null) {
            getView().displayError("You must be logged in to edit preferences.");
            return;
        }

        // only students can do this
        if (!(getCurrentUser() instanceof Student)) {

            getView().displayError("Only students can edit preferences.");
            return;
        }

        Student student = (Student) getCurrentUser();

        // show what they currently have if anything
        if (student.getStudentPreferences() != null) {

            getView().displaySuccess("Current " + student.getStudentPreferences().toString());
        }

        String rawPreferences = getView().getInput(
                "Enter your preferred event types (comma-separated, e.g. Music,Dance,Sports): ");

        // if they dont have preferences yet we need to create the object
        StudentPreferences preferences = student.getStudentPreferences();

        if (preferences == null) {
            preferences = new StudentPreferences(student);
            student.setStudentPreferences(preferences);
        }

        boolean success = preferences.updatePreferences(rawPreferences);

        if (success) {

            getView().displaySuccess("Preferences updated successfully. " + preferences.toString());

        } else {

            getView().displayError(
                    "No valid preferences were recognised. Valid types: Music, Theatre, Dance, Movie, Sports.");
        }
    }


    private boolean EPAccountAlreadyExists(
            String email,
            String orgName,
            String businessNumber) {
        for (User user : users) {
            if (!(user instanceof EntertainmentProvider)) {
                continue;
            }

            EntertainmentProvider ep = (EntertainmentProvider) user;

            boolean sameEmail = ep.getEmail().equalsIgnoreCase(email);
            boolean sameOrg = ep.getOrgName().equalsIgnoreCase(orgName) && ep.getBusinessNumber().equals(businessNumber);

            if (sameEmail || sameOrg) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a user to the collection.
     *
     * @param user
     */
    public void addUser(User user) {
        users.add(user);
    }
}
