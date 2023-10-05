package space.gavinklfong.supermarket.dtos;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Value
public class Order {
    String id;
    LocalDateTime timestamp;
    Double orderSum;
    OrderStatus status;
}
