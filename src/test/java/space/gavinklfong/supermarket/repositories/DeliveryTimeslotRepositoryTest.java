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
import space.gavinklfong.supermarket.models.Customer;
import space.gavinklfong.supermarket.models.DeliveryTimeslot;
import space.gavinklfong.supermarket.models.DeliveryTimeslotKey;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class DeliveryTimeslotRepositoryTest {

    @Container
    public static CassandraContainer container = new CassandraContainer("cassandra").withInitScript("cassandra_init.cql");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.keyspace-name", () -> KEYSPACE);
        registry.add("spring.data.cassandra.contact-points", () -> container.getContainerIpAddress());
        registry.add("spring.data.cassandra.port", () -> container.getMappedPort(9042));
    }

    @Autowired
    private DeliveryTimeslotRepository deliveryTimeslotRepository;

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
    void testSave() {
        DeliveryTimeslotKey key = DeliveryTimeslotKey.builder()
                .deliveryDate(LocalDate.of(2022, 02, 15))
                .startTime(LocalTime.of(9, 0))
                .deliveryTeamId(1)
                .build();

        DeliveryTimeslot timeslot = DeliveryTimeslot.builder()
                .reservedByCustomerId(UUID.randomUUID())
                .status(DeliveryTimeslot.Status.AVAILABLE)
                .build();

        Mono<DeliveryTimeslot> savedMono = deliveryTimeslotRepository.save(timeslot);
    }

    @Test
    void testFindById() {
        DeliveryTimeslotKey key = DeliveryTimeslotKey.builder()
                .deliveryDate(LocalDate.of(2022, 02, 15))
                .startTime(LocalTime.of(9, 0))
                .deliveryTeamId(1)
                .build();

        Mono<DeliveryTimeslot> timeslotMono = deliveryTimeslotRepository.findById(key);

        assertThat(timeslotMono).isNotNull();
        DeliveryTimeslot timeslot = timeslotMono.block();
        assertThat(timeslot).isNotNull();
        assertThat(timeslot.getKey().getDeliveryDate()).isEqualTo(LocalDate.of(2022, 02, 15));
    }

    @Test
    void testFindByDeliveryDate() {
        LocalDate deliveryDate = LocalDate.of(2022, 2, 15);
        Flux<DeliveryTimeslot> timeslotFlux = deliveryTimeslotRepository.findByDeliveryDate(deliveryDate);

        assertThat(timeslotFlux).isNotNull();
        List<DeliveryTimeslot> timeslots = timeslotFlux.collectList().block();
        assertThat(timeslots).isNotNull().hasSize(4);
    }

}
