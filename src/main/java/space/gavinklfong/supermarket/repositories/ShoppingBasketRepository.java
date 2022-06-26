package space.gavinklfong.supermarket.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.ReservedDeliveryTimeslot;
import space.gavinklfong.supermarket.models.ShoppingBasket;
import space.gavinklfong.supermarket.services.DateTimeProvider;

import java.util.HashMap;
import java.util.UUID;

import static org.springframework.data.cassandra.core.query.Criteria.where;
import static org.springframework.data.cassandra.core.query.Query.query;

@Service
public class ShoppingBasketRepository {

    @Autowired
    private ReactiveCassandraOperations cassandraOperations;

    @Autowired
    private DateTimeProvider dateTimeProvider;

    public Mono<ShoppingBasket> findById(UUID customerId) {
        return cassandraOperations
                .selectOne(
                        query(where("customer_id").is(customerId)),
                        ShoppingBasket.class);
    }

    public Mono<ShoppingBasket> save(ShoppingBasket shoppingBasket) {
        return cassandraOperations.insert(shoppingBasket);
    }

    public Mono<Void> updateReservedDeliveryTimeslot(UUID customerId, ReservedDeliveryTimeslot reservedDeliveryTimeslot) {
        return cassandraOperations.update(
                query(where("customer_id").is(customerId)),
                Update.update("reserved_delivery_timeslot", reservedDeliveryTimeslot),
                ShoppingBasket.class
        ).flatMap(result -> {
            if (!result.booleanValue())
                return cassandraOperations.insert(ShoppingBasket.builder()
                        .reservedDeliveryTimeslot(reservedDeliveryTimeslot)
                        .customerId(customerId)
                        .products(new HashMap<>())
                        .build());
            else return Mono.empty();
        }).flatMap(result -> Mono.empty());
    }
}
