package space.gavinklfong.supermarket.dtos;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Builder
@Value
public class OrderRequest {
    Double orderSum;
    Map<String, Integer> products;
}
