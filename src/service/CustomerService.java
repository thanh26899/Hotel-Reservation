package service;

import model.Customer;

import java.util.HashSet;
import java.util.Set;

public class CustomerService {
    private static final CustomerService instance = new CustomerService();
    public static CustomerService getInstance() {
        return CustomerService.instance;
    }

    private CustomerService() {
    }

    private Set<Customer> customers = new HashSet<>();
    public void addCustomer(String email, String firstName, String lastName) {
        Customer customer = new Customer(email, firstName, lastName);
        customers.add(customer);
    }

    public Customer getCustomer(String customerEmail) {
        for (Customer customer : customers) {
            if (customerEmail.equalsIgnoreCase(customer.getEmail())) {
                return customer;
            }
        }
        return null;
    }

    public Set<Customer> getAllCustomers() {
        return customers;
    }
}
