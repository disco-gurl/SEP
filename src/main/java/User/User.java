package User;

public abstract class User {
    private String email;
    private String password;

    /**
     * Constructor with the email and password given.
     *
     * @param email
     * @param password
     */
    protected User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks the provided password matches the user's.
     *
     * @param password
     * @return matches?
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
