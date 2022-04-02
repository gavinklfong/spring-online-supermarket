package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("delivery_timeslot")
public class DeliveryTimeslot {

    @PrimaryKey
    DeliveryTimeslotKey key;

    Status status;
    UUID reservedByCustomerId;

    public enum Status {
        AVAILABLE,
        RESERVED
    }
}
