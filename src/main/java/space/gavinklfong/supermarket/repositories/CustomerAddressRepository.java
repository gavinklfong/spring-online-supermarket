package space.gavinklfong.supermarket.repositories;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import space.gavinklfong.supermarket.models.CustomerAddress;
import space.gavinklfong.supermarket.models.CustomerAddressKey;

import java.util.UUID;

public interface CustomerAddressRepository extends ReactiveCrudRepository<CustomerAddress, CustomerAddressKey> {

    @Query("SELECT * FROM customer_addresses WHERE customer_id = ?0")
    Flux<CustomerAddress> findByCustomerId(UUID customerId);
}
