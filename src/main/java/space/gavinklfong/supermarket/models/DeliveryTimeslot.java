package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import space.gavinklfong.supermarket.utils.CommonUtils;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("delivery_timeslot")
public class DeliveryTimeslot {

    @PrimaryKey
    DeliveryTimeslotKey key;

    @Column("reserved_by_customer_id")
    UUID reservedByCustomerId;

    @Column("reservation_expiry")
    Instant reservationExpiry;

    @Column("order_id")
    UUID orderId;

    public Status getStatus() {
        if (nonNull(orderId) && !orderId.equals(CommonUtils.emptyUUID())) return Status.CONFIRMED;

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
