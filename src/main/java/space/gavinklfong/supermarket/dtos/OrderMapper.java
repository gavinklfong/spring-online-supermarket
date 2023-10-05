package space.gavinklfong.supermarket.dtos;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(imports = UUID.class )
public interface OrderMapper {

    @Mapping(target="orderSum", source="orderSum", numberFormat="$#.00")
    @Mapping(target="timestamp", source="timestamp", dateFormat="yyyy-MM-dd HH:mm")
    OrderApiResponse orderToOrderApiResponse(Order order);


}
