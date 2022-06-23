package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("orders_by_delivery_date")
public class OrderByDeliveryDate {

    @PrimaryKey
    OrderByDeliveryDateKey key;

    @Column("order_id")
    UUID orderId;

    @Column("customer_id")
    UUID customerId;

    @Column("delivery_address_key")
    String deliveryAddressKey;

    @Column("status")
    OrderStatus status;

    @Column("products")
    Map<UUID, Integer> products;

    @Column("remarks")
    String remarks;
}
