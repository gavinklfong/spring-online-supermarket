package space.gavinklfong.supermarket.dtos;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Builder
@Value
public class DeliveryOrder {
    String orderId;
    Double orderTotal;
    Map<String, Integer> products;
}
