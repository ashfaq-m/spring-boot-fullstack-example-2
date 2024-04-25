package com.amigoscode.customer;

import com.amigoscode.exception.CustomerNotFoundException;
import com.amigoscode.exception.DublicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO ;

    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomer(){

        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer id){
        return customerDAO.selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("customer with id %s is not found".formatted(id))
                );
    }


    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){

        // check if email exist
        if(customerDAO.existsPersonWithEmail(customerRegistrationRequest.email())){
            throw new DublicateResourceException("EMail already taken");
        }

        // add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomer(Integer customerId){
        // check if email exist
        if(customerDAO.existsCustomerWithId(customerId)) {
            // delete customer
            customerDAO.deleteCustomer(customerId);
        }else {
            throw new CustomerNotFoundException("Customer id %s not found".formatted(customerId));
        }
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest customerUpdateRequest){

        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())){
            customer.setName(customerUpdateRequest.name());
            changes = true;
        }

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())){
            if (customerDAO.existsPersonWithEmail(customerUpdateRequest.email())){
                throw new DublicateResourceException("Email already taken");
            }
            customer.setEmail(customerUpdateRequest.email());
            changes = true;
        }

        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customer.getAge())){
            customer.setAge(customerUpdateRequest.age());
            changes = true;
        }

        if (!changes){
            throw new RequestValidationException("No data changes found");
        }

        customerDAO.updateCustomer(customer);
    }

}
