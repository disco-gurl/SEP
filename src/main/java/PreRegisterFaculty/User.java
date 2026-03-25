package PreRegisterFaculty;

public abstract class User {
    String email;
    String password;

    protected User(String email, String password) {
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public boolean check(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
}
