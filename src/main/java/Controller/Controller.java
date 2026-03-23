package Controller;

import User.User;
import View.View;

public abstract class Controller {
    protected static User currentUser;
    protected static View view;

    protected Controller(View view){
        currentUser = null;
        Controller.view = view;
    }
}
