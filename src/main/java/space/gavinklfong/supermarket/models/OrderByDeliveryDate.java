package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("order_by_delivery_date")
public class OrderByDeliveryDate {
    @PrimaryKeyColumn(name = "delivery_date", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    LocalDate deliveryDate;
    @PrimaryKeyColumn(name = "start_time", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    LocalTime startTime;
    @PrimaryKeyColumn(name = "customer_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    UUID customerId;

    UUID orderId;

    Map<UUID, Integer> products;
}
