package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("order_by_customer")
public class OrderByCustomer {
    @PrimaryKeyColumn(name = "customer_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    UUID customerId;
    @PrimaryKeyColumn(name = "order_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    UUID orderId;
    @Column("delivery_address_key")
    String deliveryAddressKey;
    Map<UUID, Integer> products;
}
