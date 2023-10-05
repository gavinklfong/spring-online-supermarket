package space.gavinklfong.supermarket.dtos;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class OrderApiResponse {
    String id;
    String timestamp;
    String orderSum;
    String status;
}
