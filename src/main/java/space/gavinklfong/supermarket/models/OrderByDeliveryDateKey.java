package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyClass
public class OrderByDeliveryDateKey {
    @PrimaryKeyColumn(name = "delivery_date", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    LocalDate deliveryDate;
    @PrimaryKeyColumn(name = "delivery_team_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    UUID deliveryTeamId;
    @PrimaryKeyColumn(name = "start_time", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    LocalTime startTime;
}
