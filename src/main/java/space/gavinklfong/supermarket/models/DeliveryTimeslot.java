package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("delivery_timeslots")
public class DeliveryTimeslot {

    @PrimaryKey
    DeliveryTimeslotKey key;

    @Column("reserved_by_customer_id")
    UUID reservedByCustomerId;

    @Column("reservation_expiry")
    Instant reservationExpiry;

    @Column("confirmed")
    Boolean confirmed;

    public Status getStatus() {
        if (nonNull(confirmed) && confirmed.booleanValue()) return Status.CONFIRMED;

        if (nonNull(reservationExpiry) && reservationExpiry.isAfter(Instant.now())) {
            return Status.RESERVED;
        } else {
            return Status.AVAILABLE;
        }
    }

    public enum Status {
        AVAILABLE,
        RESERVED,
        CONFIRMED
    }
}
