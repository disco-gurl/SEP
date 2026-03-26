package User;

import Booking.Booking;
import java.util.ArrayList;
import java.util.Collection;

public class Student extends User {
    private StudentPreferences studentPreferences;
    private String name;
    private int phoneNumber;
    private Collection<Booking> bookings;

    public Student(String email, String password, String name, int phoneNumber) {
        super(email, password);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.bookings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public StudentPreferences getStudentPreferences() {return studentPreferences;}

    public void setStudentPreferences(StudentPreferences preferences) {

        this.studentPreferences = preferences;
    }

    public Collection<Booking> getBookings() {
        return bookings;
    }

}

