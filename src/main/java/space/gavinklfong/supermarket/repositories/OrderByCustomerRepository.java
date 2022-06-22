package space.gavinklfong.supermarket.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.OrderByCustomer;

import java.util.UUID;

import static org.springframework.data.cassandra.core.query.Criteria.where;
import static org.springframework.data.cassandra.core.query.Query.query;

@Service
public class OrderByCustomerRepository {

    @Autowired
    private ReactiveCassandraOperations reactiveCassandraOperations;

    public Flux<OrderByCustomer> findByCustomerId(UUID customerId) {
        return reactiveCassandraOperations.select(query(where("customer_id").is(customerId)), OrderByCustomer.class);
    }

    public Mono<OrderByCustomer> save(OrderByCustomer orderByCustomer) {
        Mono<EntityWriteResult<OrderByCustomer>> insertResult = reactiveCassandraOperations.insert(orderByCustomer, InsertOptions.builder().ifNotExists(true).build());
        return insertResult.flatMap(result ->
            (!result.wasApplied())?
                    reactiveCassandraOperations.update(orderByCustomer) :
                    Mono.just(result.getEntity())
        );
    }

}
