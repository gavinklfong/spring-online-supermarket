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
import space.gavinklfong.supermarket.models.CustomerAddress;
import space.gavinklfong.supermarket.models.CustomerAddressKey;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CustomerAddressRepositoryTest extends CassandraRepositoryBaseTest {

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    private static final String CUSTOMER_ID = "7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3";

    @Test
    void givenCustomers_whenFindById_thenReturnCustomer() {
        CustomerAddressKey customerAddressKey = CustomerAddressKey.builder().customerId(UUID.fromString(CUSTOMER_ID)).addressKey("HOME").build();
        Mono<CustomerAddress> customerAddressMono = customerAddressRepository.findById(customerAddressKey);
        assertThat(customerAddressMono).isNotNull();
        CustomerAddress customerAddress = customerAddressMono.block();
        assertThat(customerAddress).isNotNull();
        assertThat(customerAddress.getCustomerAddressKey()).isNotNull();
        assertThat(customerAddress.getCustomerAddressKey().getCustomerId()).isEqualTo(UUID.fromString(CUSTOMER_ID));
        assertThat(customerAddress.getCustomerAddressKey().getAddressKey()).isEqualTo("HOME");
        assertThat(customerAddress.getAddress()).isNotNull();
        log.info("customer home address: {}", customerAddress.getAddress());
    }

    @Test
    void givenCustomers_whenSave_thenReturnUpdatedCustomer() {
        CustomerAddressKey customerAddressKey = CustomerAddressKey.builder().customerId(UUID.fromString(CUSTOMER_ID)).addressKey("HOME").build();
        Mono<CustomerAddress> customerAddressMono = customerAddressRepository.findById(customerAddressKey);
        assertThat(customerAddressMono).isNotNull();
        CustomerAddress customerAddress = customerAddressMono.block();

        Address address = customerAddress.getAddress();
        Address updatedAddress = address.toBuilder().streetAddress("updated " + address.getStreetAddress()).build();
        CustomerAddress updatedCustomerAddress = customerAddress.toBuilder().address(updatedAddress).build();
        Mono<CustomerAddress> saved = customerAddressRepository.save(updatedCustomerAddress);

        // wait for the save to be completed
        saved.block();

        // get the updated customer
        Mono<CustomerAddress> newCustomerAddressMono = customerAddressRepository.findById(customerAddressKey);
        assertThat(newCustomerAddressMono).isNotNull();
        CustomerAddress newCustomerAddress = newCustomerAddressMono.block();
        assertThat(newCustomerAddress).isNotNull();
        assertThat(newCustomerAddress.getAddress()).isEqualTo(updatedAddress);
    }

}
