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
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.Customer;
import space.gavinklfong.supermarket.models.ShoppingBasket;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class ShoppingBasketRepositoryTest {

    @Container
    public static CassandraContainer container = new CassandraContainer("cassandra").withInitScript("cassandra_init.cql");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.keyspace-name", () -> KEYSPACE);
        registry.add("spring.data.cassandra.contact-points", () -> container.getContainerIpAddress());
        registry.add("spring.data.cassandra.port", () -> container.getMappedPort(9042));
    }

    @Autowired
    private ShoppingBasketRepository shoppingBasketRepository;

    private static final String KEYSPACE = "supermarket";
    private static final String CUSTOMER_ID = "7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3";

//    @BeforeAll
//    static void createKeyspace() {
//        try (Session session = container.getCluster().connect()) {
//            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE + " WITH replication = " +
//                    "{'class':'SimpleStrategy','replication_factor':'1'};");
//        }
//    }

    @Test
    void givenBasketRecord_whenFindByCustomerId_thenReturnBasket() {
        Mono<ShoppingBasket> basketMono = shoppingBasketRepository.findById(UUID.fromString(CUSTOMER_ID));
        assertThat(basketMono).isNotNull();
        ShoppingBasket basket = basketMono.block();
        assertThat(basket).isNotNull();
        assertThat(basket.getCustomerId()).isEqualTo(UUID.fromString(CUSTOMER_ID));
        assertThat(basket.getProducts()).hasSizeGreaterThan(0);
    }

    @Test
    void givenCustomers_whenSave_thenReturnUpdatedCustomer() {
        Mono<ShoppingBasket> basketMono = shoppingBasketRepository.findById(UUID.fromString(CUSTOMER_ID));
        assertThat(basketMono).isNotNull();
        ShoppingBasket basket = basketMono.block();

        Map<UUID, Integer> products = basket.getProducts();
        int originalBasketSize = products.size();
        UUID newProductId = UUID.randomUUID();
        products.put(newProductId, 10);

        Mono<ShoppingBasket> saved = shoppingBasketRepository.save(basket);

        // wait for the save to be completed
        saved.block();

        // get the updated customer
        Mono<ShoppingBasket> updatedBasketMono = shoppingBasketRepository.findById(UUID.fromString(CUSTOMER_ID));
        assertThat(updatedBasketMono).isNotNull();
        ShoppingBasket updatedBasket = updatedBasketMono.block();
        assertThat(updatedBasket).isNotNull();
        assertThat(updatedBasket.getProducts()).hasSize(originalBasketSize + 1);
        assertThat(updatedBasket.getProducts().get(newProductId)).isEqualTo(10);
    }

}
