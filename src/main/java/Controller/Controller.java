package Controller;

import User.User;
import User.AdminStaff;
import User.Student;
import User.EntertainmentProvider;
import View.View;

public abstract class Controller {
    protected static User currentUser;
    protected static View view;

    /**
     * The constructor of a controller with the given view.
     *
     * @param view
     */
    protected Controller(View view) {
        this.currentUser = null;
        this.view = view;
    }

    protected View getView() {
        return view;
    }

    protected User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Methods from class diagram.
     *
     * @return
     */
    protected boolean checkCurrentUserIsGuest() {
        return currentUser == null;
    }

    private boolean checkCurrentUserIsAdmin() {
        return currentUser instanceof AdminStaff;
    }

    protected boolean checkCurrentUserIsStudent() {
        return currentUser instanceof Student;
    }

    private boolean checkCurrentUserIsEntertainmentProvider() {
        return currentUser instanceof EntertainmentProvider;
    }


}