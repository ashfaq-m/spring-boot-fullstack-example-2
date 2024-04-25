package com.amigoscode.customer;

import com.amigoscode.exception.CustomerNotFoundException;
import com.amigoscode.exception.DublicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    private CustomerService serviceUnderTest;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomer() {
        // When
        serviceUnderTest.getAllCustomer();

        // Then
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                19
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = serviceUnderTest.getCustomer(10);

        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        int id = 10;

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> serviceUnderTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "customer with id %s is not found".formatted(id));

    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );

        // When
        serviceUnderTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(captor.capture());
        Customer actual = captor.getValue();

        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo(request.name());
        assertThat(actual.getEmail()).isEqualTo(request.email());
        assertThat(actual.getAge()).isEqualTo(request.age());
    }


    @Test
    void willThrowWknEmailExistsWhileAddingACustomerO() {
        // Given
        String email = "alex@gmail.com";

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );

        // When
        assertThatThrownBy(() -> serviceUnderTest.addCustomer(request))
                .isInstanceOf(DublicateResourceException.class)
                .hasMessage("EMail already taken");

        //Then
        verify(customerDAO, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomer() {
        // Given
        int id = 10;

        when(customerDAO.existsCustomerWithId(id)).thenReturn(true);

        // When
        serviceUnderTest.deleteCustomer(id);

        // Then
        verify(customerDAO).deleteCustomer(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        // Given
        int id = 10;

        when(customerDAO.existsCustomerWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> serviceUnderTest.deleteCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer id %s not found".formatted(id));


        // Then
        verify(customerDAO, never()).deleteCustomer(id);
    }


    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));


        String newEmail = "Alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", newEmail, 23
        );

        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        // when
        serviceUnderTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer actual = argumentCaptor.getValue();

        assertThat(actual.getName()).isEqualTo(updateRequest.name());
        assertThat(actual.getEmail()).isEqualTo(updateRequest.email());
        assertThat(actual.getAge()).isEqualTo(updateRequest.age());

    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro",null, null
        );


        // when
        serviceUnderTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer actual = argumentCaptor.getValue();

        assertThat(actual.getName()).isEqualTo(updateRequest.name());
        assertThat(actual.getEmail()).isEqualTo(customer.getEmail());
        assertThat(actual.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "Alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,newEmail, null
        );

        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        // when
        serviceUnderTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer actual = argumentCaptor.getValue();

        assertThat(actual.getName()).isEqualTo(customer.getName());
        assertThat(actual.getEmail()).isEqualTo(newEmail);
        assertThat(actual.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,null, 12
        );

        // when
        serviceUnderTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer actual = argumentCaptor.getValue();

        assertThat(actual.getName()).isEqualTo(customer.getName());
        assertThat(actual.getEmail()).isEqualTo(customer.getEmail());
        assertThat(actual.getAge()).isEqualTo(updateRequest.age());

    }


    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "Alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,newEmail, null
        );

        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(true);

        // when
        assertThatThrownBy(() -> serviceUnderTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DublicateResourceException.class)
                .hasMessageContaining("Email already taken");

        // Then
        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        // when
        assertThatThrownBy(() -> serviceUnderTest.updateCustomer(id, updateRequest))
        .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("No data changes found");

        // Then
        verify(customerDAO, never()).updateCustomer(any());

    }

}