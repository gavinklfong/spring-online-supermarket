package space.gavinklfong.supermarket.utils;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import space.gavinklfong.supermarket.models.Order;
import space.gavinklfong.supermarket.models.OrderByCustomer;
import space.gavinklfong.supermarket.models.OrderByDeliveryDate;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderByCustomer orderToOrderByCustomer(Order order);
    Order orderByCustomerToOrder(OrderByCustomer orderByCustomer);

    OrderByDeliveryDate orderToOrderByDeliveryDate(Order order);
    Order orderByDeliveryDateToOrder(OrderByDeliveryDate orderByDeliveryDate);
}
