package space.gavinklfong.supermarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.repositories.ShoppingBasketRepository;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
public class ShoppingService {

    @Autowired
    private ShoppingBasketRepository shoppingBasketRepository;

//    public Mono<Void> addProductToBasket(UUID customerId, UUID productCode, int quantity) {
//        shoppingBasketRepository.findById(customerId)
//                .map(basket -> {
//                    Map<UUID, Integer> products = basket.getProducts();
//                    if(nonNull(products)) products = new Hashtable<>();
//                    products.compute(productCode, (count) -> count+quantity);
//                })
//    }
}
