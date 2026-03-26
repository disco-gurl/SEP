package PreRegisterFaculty;

public abstract class User {
    String email;
    String password;

    protected User(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean check(String email, String password) {
        return this.getEmail().equals(email) && this.getPassword().equals(password);
    }
}
