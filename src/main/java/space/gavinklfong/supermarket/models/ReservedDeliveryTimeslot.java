package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@UserDefinedType("reserved_delivery_timeslot")
public class ReservedDeliveryTimeslot {
    @Column("delivery_date")
    LocalDate deliveryDate;

    @Column("start_time")
    LocalTime startTime;

    @Column("delivery_team_id")
    UUID deliveryTeamId;

    @Column("reservation_expiry")
    Instant reservationExpiry;
}
