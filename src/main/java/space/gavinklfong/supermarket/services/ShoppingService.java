package space.gavinklfong.supermarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.DeliveryTimeslot;
import space.gavinklfong.supermarket.models.DeliveryTimeslotKey;
import space.gavinklfong.supermarket.models.ShoppingBasket;
import space.gavinklfong.supermarket.repositories.DeliveryTimeslotRepository;
import space.gavinklfong.supermarket.repositories.ShoppingBasketRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.util.Objects.isNull;
import static space.gavinklfong.supermarket.utils.CommonUtils.EMPTY_UUID;

@Service
public class ShoppingService {

    public final static Duration RESERVATION_VALID_PERIOD = Duration.of(1, ChronoUnit.HOURS);

    @Autowired
    private ShoppingBasketRepository shoppingBasketRepository;

    @Autowired
    private DeliveryTimeslotRepository deliveryTimeslotRepository;

    @Autowired
    private DateTimeProvider dateTimeProvider;

    public Mono<ShoppingBasket> updateProductToBasket(UUID customerId, UUID productCode, int quantity) {
        if (isNull(customerId) && customerId.equals(EMPTY_UUID)) throw new IllegalArgumentException("invalid customer id");
        if (isNull(productCode) && productCode.equals(EMPTY_UUID)) throw new IllegalArgumentException("invalid product code");
        if (quantity < 0) throw new IllegalArgumentException("product quantity should not be negative");

        return shoppingBasketRepository.findById(customerId)
                .map(basket -> basket.getProducts())
                .flatMap(selectedProducts -> {
                    if (quantity == 0) selectedProducts.remove(productCode);
                    else selectedProducts.put(productCode, quantity);
                    return shoppingBasketRepository.save(
                            ShoppingBasket.builder().customerId(customerId)
                                    .products(selectedProducts).build());
                });
    }

    public Mono<Void> clearBasket(UUID customerId) {
        if (isNull(customerId) && customerId.equals(EMPTY_UUID)) throw new IllegalArgumentException("invalid customer id");
        return shoppingBasketRepository.deleteById(customerId);
    }

    public Flux<DeliveryTimeslot> findDeliveryTimeslotsByDate(LocalDate deliveryDate) {
        if (deliveryDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("delivery date should be a future date");
        return deliveryTimeslotRepository.findByDeliveryDate(deliveryDate);
    }

    public Mono<Boolean> reserveDeliveryTimeSlot(DeliveryTimeslotKey key, UUID customerId) {
        return deliveryTimeslotRepository.updateReservedCustomer(key, customerId, dateTimeProvider.now().plus(RESERVATION_VALID_PERIOD));
    }
}
