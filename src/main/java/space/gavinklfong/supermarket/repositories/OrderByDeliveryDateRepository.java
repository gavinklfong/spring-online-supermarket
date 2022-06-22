package space.gavinklfong.supermarket.repositories;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import space.gavinklfong.supermarket.models.OrderByDeliveryDate;
import space.gavinklfong.supermarket.models.OrderByDeliveryDateKey;

import java.time.LocalDate;
import java.util.UUID;

public interface OrderByDeliveryDateRepository extends ReactiveCrudRepository<OrderByDeliveryDate, OrderByDeliveryDateKey> {

//    @Query("SELECT o FROM OrderByDeliveryDate o WHERE o.key.deliveryDate = ?1")
    @Query("SELECT * FROM orders_by_delivery_date WHERE delivery_date = ?0 AND delivery_team_id = ?1")
    Flux<OrderByDeliveryDate> findByDeliveryDate(LocalDate deliveryDate, UUID deliveryTeamId);
}
