package PreRegisterFaculty;

import View.View;

public abstract class Controller {
    protected static User currentUser;
    protected static View view;

    protected Controller(View view) {
        this.currentUser = null;
        this.view = view;
    }


}