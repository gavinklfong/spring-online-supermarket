package space.gavinklfong.supermarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.RecordNotFoundException;
import space.gavinklfong.supermarket.models.Customer;
import space.gavinklfong.supermarket.repositories.CustomerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

//    public Mono<Customer> saveAddress(UUID customerId, String addressKey, String address) {
//        return customerRepository.findById(customerId)
//                .switchIfEmpty(Mono.error(new RecordNotFoundException()))
//                .map(customer -> {
//                    Map<String, String> addresses = customer.getAddresses();
//                    if (isNull(addresses)) addresses = new HashMap<>();
//                    addresses.put(addressKey, address);
//                    return customer.toBuilder().addresses(addresses).build();
//                })
//                .flatMap(updatedCustomer ->
//                    customerRepository.save(updatedCustomer)
//                );
//    }

    public Mono<Customer> findByCustomerId(UUID customerId) {
        return customerRepository.findById(customerId);
    }
}
