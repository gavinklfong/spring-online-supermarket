package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Frozen;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("shopping_basket_by_customer")
public class ShoppingBasket {
    @PrimaryKey("customer_id")
    UUID customerId;

    @Frozen
    Map<UUID, Integer> products;

    @Frozen
    @Column("reserved_delivery_timeslot")
    ReservedDeliveryTimeslot reservedDeliveryTimeslot;
}
