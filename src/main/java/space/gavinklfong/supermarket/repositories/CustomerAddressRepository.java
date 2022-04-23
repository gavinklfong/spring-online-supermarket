package space.gavinklfong.supermarket.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import space.gavinklfong.supermarket.models.CustomerAddress;
import space.gavinklfong.supermarket.models.CustomerAddressKey;

public interface CustomerAddressRepository extends ReactiveCrudRepository<CustomerAddress, CustomerAddressKey> {
}
