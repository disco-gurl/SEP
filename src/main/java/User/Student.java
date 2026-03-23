package User;

import Booking.Booking;
import java.util.ArrayList;
import java.util.Collection;

public class Student extends User {
    private StudentPreferences studentPreferences;
    private String name;
    private int phoneNumber;
    private Collection<Booking> bookings;

    public Student(String email, String password, String name, int phonenumber) {
        super(email, password);
        this.name = name;
        this.phoneNumber = phonenumber;
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

    public Collection<Booking> getBookings() {
        return bookings;
    }
}