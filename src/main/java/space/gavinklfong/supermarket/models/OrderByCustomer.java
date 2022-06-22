package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.Currency;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("orders_by_customer")
public class OrderByCustomer {

    @PrimaryKeyColumn(name = "customer_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    UUID customerId;
    @PrimaryKeyColumn(name = "order_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    UUID orderId;
    @PrimaryKeyColumn(name = "submission_time", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    Instant submissionTime;

    @Column("products")
    Map<UUID, Integer> products;

    @Column("status")
    OrderStatus status;

    @Column("delivery_address_key")
    String deliveryAddressKey;

    @Column("currency")
    String currency;

    @Column("item_subtotal")
    Double itemSubTotal;

    @Column("postage_packing_fee")
    Double postagePackingFee;

    @Column("promotional_discount")
    Double promotionalDiscount;

    @Column("vat")
    Double vat;

    @Column("remarks")
    String remarks;
}
