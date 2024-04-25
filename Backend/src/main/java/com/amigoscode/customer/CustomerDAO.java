package com.amigoscode.customer;

import java.util.List;
import java.util.Optional;


public interface CustomerDAO {

    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    void deleteCustomer(Integer customerId);

    boolean existsCustomerWithId(Integer id);

    void updateCustomer(Customer update);
}
