package space.gavinklfong.supermarket.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import space.gavinklfong.supermarket.models.Customer;

import java.util.UUID;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, UUID> {

}
