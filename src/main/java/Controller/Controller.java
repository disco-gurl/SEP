package Controller;

abstract class Controller {
    private User currentUser;
    private View view;

    protected Controller(View view){
        this.currentUser = null;
        this.view = view;
    }
}
