package space.gavinklfong.supermarket.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import space.gavinklfong.supermarket.models.ShoppingBasket;

import java.util.UUID;

public interface ShoppingBasketRepository extends ReactiveCrudRepository<ShoppingBasket, UUID> {
}
