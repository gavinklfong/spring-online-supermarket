package space.gavinklfong.supermarket.dtos;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import space.gavinklfong.supermarket.models.OrderStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ShoppingOrder {

    UUID customerId;
    UUID orderId;
    Instant submissionTime;
    Map<UUID, Integer> products;
    String deliveryAddressKey;
    String currency;
    Double itemSubTotal;
    Double postagePackingFee;
    Double promotionalDiscount;
    Double vat;
    String remarks;
}
