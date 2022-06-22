package space.gavinklfong.supermarket.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.OrderByCustomer;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class OrderByCustomerRepositoryTest extends CassandraRepositoryBaseTest {

    @Autowired
    private OrderByCustomerRepository repository;

    private static final UUID CUSTOMER_ID = UUID.fromString("28a76352-0102-45ae-81c7-aeb25cc228f9");
    
    @Test
    void givenOrders_whenFindByCustomerId_thenReturnOrders() {
        Flux<OrderByCustomer> orderFlux = repository.findByCustomerId(CUSTOMER_ID);
        assertThat(orderFlux).isNotNull();
        List<OrderByCustomer> orders = orderFlux.collectList().block();
        assertThat(orders).isNotNull();
        assertThat(orders).hasSizeGreaterThan(0);
    }

    @Test
    void givenAnOrder_whenSave_thenNewOrderAdded() {
        Flux<OrderByCustomer> queryResult1 = repository.findByCustomerId(CUSTOMER_ID);
        int originalCount = queryResult1.collectList().block().size();

        OrderByCustomer order = OrderByCustomer.builder()
                .customerId(CUSTOMER_ID)
                .orderId(UUID.randomUUID())
                .products(Map.of(UUID.randomUUID(), 5, UUID.randomUUID(), 2))
                .deliveryAddressKey("HOME")
                .submissionTime(Instant.now())
                .build();

        Mono<OrderByCustomer> savedOrder = repository.save(order);
        assertThat(savedOrder).isNotNull();
        savedOrder.block();

        Flux<OrderByCustomer> queryResult2 = repository.findByCustomerId(CUSTOMER_ID);
        int updatedCount = queryResult2.collectList().block().size();

        assertThat(updatedCount).isEqualTo(originalCount + 1);
    }

}
