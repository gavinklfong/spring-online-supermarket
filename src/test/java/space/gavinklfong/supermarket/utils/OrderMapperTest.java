package space.gavinklfong.supermarket.utils;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import space.gavinklfong.supermarket.models.Order;
import space.gavinklfong.supermarket.models.OrderByCustomer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderMapperTest {

    static private final Map<UUID, Integer> PRODUCTS = Map.of(UUID.randomUUID(), RandomUtils.nextInt());
    static private final LocalDate DELIVERY_DATE = LocalDate.of(2022, 3, 1);
    static private final LocalTime START_TIME = LocalTime.of(10, 0);

    private OrderMapper orderMapper = OrderMapper.INSTANCE;

    @Test
    void testOrderToOrderByCustomer() {
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .deliveryDate(DELIVERY_DATE)
                .startTime(START_TIME)
                .products(PRODUCTS)
                .build();

        OrderByCustomer orderByCustomer = orderMapper.orderToOrderByCustomer(order);

        assertThat(orderByCustomer.getCustomerId()).isEqualTo(order.getCustomerId());
        assertThat(orderByCustomer.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(orderByCustomer.getDeliveryDate()).isEqualTo(order.getDeliveryDate());
        assertThat(orderByCustomer.getStartTime()).isEqualTo(order.getStartTime());
        assertThat(orderByCustomer.getProducts()).isEqualTo(order.getProducts());
    }
}
