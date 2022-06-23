package space.gavinklfong.supermarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.RecordNotFoundException;
import space.gavinklfong.supermarket.models.Address;
import space.gavinklfong.supermarket.models.Customer;
import space.gavinklfong.supermarket.models.CustomerAddress;
import space.gavinklfong.supermarket.models.CustomerAddressKey;
import space.gavinklfong.supermarket.repositories.CustomerAddressRepository;
import space.gavinklfong.supermarket.repositories.CustomerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    public Mono<CustomerAddress> saveAddress(UUID customerId, String addressKey, Address address) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new RecordNotFoundException()))
                .flatMap(customer -> {
                    CustomerAddressKey customerAddressKey = CustomerAddressKey.builder().addressKey(addressKey).customerId(customerId).build();
                    return customerAddressRepository.save(
                            CustomerAddress.builder()
                                    .customerAddressKey(customerAddressKey)
                                    .address(address)
                                    .build());
                });
    }

    public Flux<CustomerAddress> findCustomerAddresses(UUID customerId) {
        return customerAddressRepository.findByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new RecordNotFoundException()));
    }

    public Mono<Customer> saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Customer> findByCustomerId(UUID customerId) {
        return customerRepository.findById(customerId);
    }
}
