package space.gavinklfong.supermarket.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.UpdateOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.DeliveryTimeslot;
import space.gavinklfong.supermarket.models.DeliveryTimeslotKey;
import space.gavinklfong.supermarket.services.DateTimeProvider;
import space.gavinklfong.supermarket.utils.CommonUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.data.cassandra.core.query.Criteria.where;
import static org.springframework.data.cassandra.core.query.Query.query;

@Service
public class DeliveryTimeslotRepository {

    @Autowired
    private ReactiveCassandraOperations cassandraOperations;

    @Autowired
    private DateTimeProvider dateTimeProvider;

    public Mono<DeliveryTimeslot> findById(DeliveryTimeslotKey key) {
        return cassandraOperations
                .selectOne(
                        query(where("delivery_date").is(key.getDeliveryDate()))
                                .and(where("start_time").is(key.getStartTime()))
                                .and(where("delivery_team_id").is(key.getDeliveryTeamId())),
                DeliveryTimeslot.class);
    }

    public Flux<DeliveryTimeslot> findByDeliveryDate(LocalDate deliveryDate) {
        return cassandraOperations.select(query(where("delivery_date").is(deliveryDate)), DeliveryTimeslot.class);
    }

    public Mono<DeliveryTimeslot> save(DeliveryTimeslot deliveryTimeslot) {
        return cassandraOperations.insert(deliveryTimeslot);
    }

    public Mono<Boolean> updateReservedCustomer(DeliveryTimeslotKey key, UUID customerId, Instant reservationExpiry) {
        DeliveryTimeslot deliveryTimeslot = DeliveryTimeslot.builder()
                .key(key)
                .reservedByCustomerId(customerId)
                .reservationExpiry(reservationExpiry)
                .build();

        return updateIfEmptyCustomerId(deliveryTimeslot)
                .flatMap(writeResult ->
                       writeResult.booleanValue()?
                            Mono.just(true):
                            updateIfReservationIsExpired(deliveryTimeslot)
                    );
    }

    private Mono<Boolean> updateIfEmptyCustomerId(DeliveryTimeslot deliveryTimeslot) {
        return cassandraOperations.update(deliveryTimeslot,
                UpdateOptions.builder().ifCondition(
                        query(where("reserved_by_customer_id").is(CommonUtils.EMPTY_UUID))
                ).build())
                .map(result -> result.wasApplied());
    }

    private Mono<Boolean> updateIfReservationIsExpired(DeliveryTimeslot deliveryTimeslot) {
        return cassandraOperations.update(deliveryTimeslot,
                        UpdateOptions.builder().ifCondition(
                                query(where("reservation_expiry").lt(dateTimeProvider.now()))
                        ).build())
                .map(result -> result.wasApplied());
    }
}
