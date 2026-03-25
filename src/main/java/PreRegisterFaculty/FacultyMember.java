package PreRegisterFaculty;

public class FacultyMember extends PreRegisterFaculty.User {
    private int loginAttempt;
    private boolean firstLogin;

    public FacultyMember (String email, String password) {
        super(email, password);
        this.loginAttempt = 0;
        this.firstLogin = true;
    }

    //getter method for login attempts
    public int getLoginAttempt() {
        return loginAttempt;
    }

    //methods to increase login attempts
    public void increaseLogins() {
        loginAttempt++ ;
    }

    //setter methods to make attempts 0
    public void resetAttempts() {
        loginAttempt = 0;
    }

    //check if it is the first log in
    public boolean isFirstLogin() {
        return firstLogin;
    }

    //setter which updates the boolean checking if first login
    public void setFirstLogin(boolean FirstLogin) {
        this.firstLogin = FirstLogin;
    }
}
