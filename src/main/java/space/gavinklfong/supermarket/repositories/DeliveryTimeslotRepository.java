package space.gavinklfong.supermarket.repositories;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import space.gavinklfong.supermarket.models.DeliveryTimeslot;
import space.gavinklfong.supermarket.models.DeliveryTimeslotKey;

import java.time.LocalDate;

public interface DeliveryTimeslotRepository extends ReactiveCrudRepository<DeliveryTimeslot, DeliveryTimeslotKey> {

    @Query("select * from delivery_timeslot where delivery_date = ?0")
    public Flux<DeliveryTimeslot> findByDeliveryDate(LocalDate date);

}
