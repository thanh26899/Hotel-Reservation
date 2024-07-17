package model;

import java.util.regex.Pattern;

public class Customer {

    public static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@([A-Za-z0-9.-]+\\.)+[cC][oO][mM]$");

    private String firstName;
    private String lastName;
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public Customer(String email, String firstName, String lastName) {

        if (!EMAIL_REGEX_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException();
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
