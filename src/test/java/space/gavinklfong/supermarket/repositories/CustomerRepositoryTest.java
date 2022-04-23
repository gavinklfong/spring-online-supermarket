package space.gavinklfong.supermarket.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.Address;
import space.gavinklfong.supermarket.models.Customer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CustomerRepositoryTest extends CassandraRepositoryBaseTest {

    @Autowired
    private CustomerRepository customerRepository;
    private static final String CUSTOMER_ID = "7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3";

    @Test
    void givenCustomers_whenFindById_thenReturnCustomer() {
        Mono<Customer> customerMono = customerRepository.findById(UUID.fromString(CUSTOMER_ID));
        assertThat(customerMono).isNotNull();
        Customer customer = customerMono.block();
        assertThat(customer).isNotNull();
        assertThat(customer.getCustomerId()).isEqualTo(UUID.fromString(CUSTOMER_ID));
//        assertThat(customer.getAddresses()).containsKey("home");
//        Address address = customer.getAddresses().get("home");
//        log.info("customer home address: {}", address);
    }

    @Test
    void givenCustomers_whenSave_thenReturnUpdatedCustomer() {
        Mono<Customer> customerMono = customerRepository.findById(UUID.fromString(CUSTOMER_ID));
        assertThat(customerMono).isNotNull();
        Customer customer = customerMono.block();

        customer.setName("Updated " + customer.getName());
        Mono<Customer> saved = customerRepository.save(customer);

        // wait for the save to be completed
        saved.block();

        // get the updated customer
        Mono<Customer> updatedCustomerMono = customerRepository.findById(UUID.fromString(CUSTOMER_ID));
        assertThat(updatedCustomerMono).isNotNull();
        Customer updatedCustomer = updatedCustomerMono.block();
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getName()).isEqualTo(customer.getName());
    }

}
