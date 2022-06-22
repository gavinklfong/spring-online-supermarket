package space.gavinklfong.supermarket.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
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
import space.gavinklfong.supermarket.utils.CommonUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static space.gavinklfong.supermarket.repositories.TestConstants.DELIVERY_TEAM_ID;

@Slf4j
public class DeliveryTimeslotRepositoryTest extends CassandraRepositoryBaseTest {
    @Autowired
    private DeliveryTimeslotRepository deliveryTimeslotRepository;

    private static final String CUSTOMER_ID = "7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3";

//    UPDATE delivery_timeslot
//    SET reserved_by_customer_id = 55a873e9-3c07-42c7-b7cd-1025a7c7beca
//    WHERE delivery_date = '2022-02-16' AND start_time = '12:00:00' AND delivery_team_id = 1
//    IF reserved_by_customer_id = 00000000-0000-0000-0000-000000000000;
//
//    UPDATE delivery_timeslot
//    SET reserved_by_customer_id = 55a873e9-3c07-42c7-b7cd-1025a7c7beca
//    WHERE delivery_date = '2022-02-16' AND start_time = '12:00:00' AND delivery_team_id = 1
//    IF reserved_by_customer_id != 00000000-0000-0000-0000-000000000000
//    AND reservation_expiry < '2011-02-03 04:05+0000'
//    AND order_id = 00000000-0000-0000-0000-000000000000;
//
//
//    UPDATE delivery_timeslot
//    SET reserved_by_customer_id = f531794d-3a6d-4f2f-89e0-c9452511cc82
//    WHERE delivery_date = '2022-02-16' AND start_time = '12:00:00' AND delivery_team_id = 1
//    IF reserved_by_customer_id != 00000000-0000-0000-0000-000000000000
//    AND reservation_expiry < '2011-02-03 04:05+0000'
//    AND order_id = 00000000-0000-0000-0000-000000000000;

    @Test
    void testSave() {
        DeliveryTimeslotKey key = DeliveryTimeslotKey.builder()
                .deliveryDate(LocalDate.of(2022, 01, 01))
                .startTime(LocalTime.of(9, 0))
                .deliveryTeamId(DELIVERY_TEAM_ID)
                .build();

        DeliveryTimeslot timeslot = DeliveryTimeslot.builder()
                .key(key)
                .reservedByCustomerId(UUID.randomUUID())
                .reservationExpiry(Instant.now().plus(10, ChronoUnit.MINUTES))
                .confirmed(false)
                .build();

        Mono<DeliveryTimeslot> savedMono = deliveryTimeslotRepository.save(timeslot);

        // wait for save
        savedMono.block();

        Mono<DeliveryTimeslot> deliveryTimeslotMono = deliveryTimeslotRepository.findById(key);
        assertThat(deliveryTimeslotMono).isNotNull();
        DeliveryTimeslot deliveryTimeslot = deliveryTimeslotMono.block();
        assertThat(deliveryTimeslot).isNotNull();
        assertThat(deliveryTimeslot.getStatus()).isEqualTo(DeliveryTimeslot.Status.RESERVED);
    }

    @Test
    void testFindById() {
        DeliveryTimeslotKey key = DeliveryTimeslotKey.builder()
                .deliveryDate(LocalDate.of(2022, 02, 15))
                .startTime(LocalTime.of(9, 0))
                .deliveryTeamId(DELIVERY_TEAM_ID)
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
