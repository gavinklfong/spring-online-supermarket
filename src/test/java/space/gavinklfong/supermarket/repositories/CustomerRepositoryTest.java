package space.gavinklfong.supermarket.repositories;

import com.datastax.driver.core.Session;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
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
import space.gavinklfong.supermarket.models.Customer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class CustomerRepositoryTest {

    @Container
    public static CassandraContainer container = new CassandraContainer("cassandra").withInitScript("cassandra_init.cql");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.keyspace-name", () -> KEYSPACE);
        registry.add("spring.data.cassandra.contact-points", () -> container.getContainerIpAddress());
        registry.add("spring.data.cassandra.port", () -> container.getMappedPort(9042));
    }

    @Autowired
    private CustomerRepository customerRepository;

    private static final String KEYSPACE = "supermarket";
    private static final String CUSTOMER_ID = "7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3";

//    @BeforeAll
//    static void createKeyspace() {
//        try (Session session = container.getCluster().connect()) {
//            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE + " WITH replication = " +
//                    "{'class':'SimpleStrategy','replication_factor':'1'};");
//        }
//    }

    @Test
    void givenCustomers_whenFindById_thenReturnCustomer() {
        Mono<Customer> customerMono = customerRepository.findById(UUID.fromString(CUSTOMER_ID));
        assertThat(customerMono).isNotNull();
        Customer customer = customerMono.block();
        assertThat(customer).isNotNull();
        assertThat(customer.getCustomerId()).isEqualTo(UUID.fromString(CUSTOMER_ID));
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
