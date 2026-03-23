package User;

public class Student extends User {
    StudentPreferences studentPreferences;
    public String name;
    public int phoneNumber;

    public Student(String email, String password) {
        super(email, password);
    }
}
