package model;

import java.util.regex.Pattern;

public class Driver {
    public static void main(String[] args) {
        Customer customer = new Customer("first", "second", "j@gmail.com");
        System.out.println(customer);
//        Customer customer1 = new Customer("first", "second", "email");
    }
}
