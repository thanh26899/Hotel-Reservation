package model;

import java.util.regex.Pattern;

public class Customer {

    public static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^\\w+([-+.']\\w+)*@[A-Za-z\\d]+\\.com$");

    private String firstName;
    private String lastName;
    private String email;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
