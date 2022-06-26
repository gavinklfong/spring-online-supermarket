package space.gavinklfong.supermarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.dtos.ShoppingOrder;
import space.gavinklfong.supermarket.models.*;
import space.gavinklfong.supermarket.repositories.DeliveryTimeslotRepository;
import space.gavinklfong.supermarket.repositories.OrderByCustomerRepository;
import space.gavinklfong.supermarket.repositories.OrderByDeliveryDateRepository;
import space.gavinklfong.supermarket.repositories.ShoppingBasketRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
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
    private OrderByCustomerRepository orderByCustomerRepository;

    @Autowired
    private OrderByDeliveryDateRepository orderByDeliveryDateRepository;

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

    public Flux<DeliveryTimeslot> findDeliveryTimeslotsByDate(LocalDate deliveryDate) {
        if (deliveryDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("delivery date should be a future date");
        return deliveryTimeslotRepository.findByDeliveryDate(deliveryDate);
    }

    public Mono<Boolean> reserveDeliveryTimeSlot(DeliveryTimeslotKey key, UUID customerId) {
        Instant expiryTime = dateTimeProvider.now().plus(RESERVATION_VALID_PERIOD);
        return deliveryTimeslotRepository.updateReservedCustomer(key, customerId, expiryTime)
                .map(reserveResult -> {
                    if (reserveResult.booleanValue()) {
                        ReservedDeliveryTimeslot reservedDeliveryTimeslot = ReservedDeliveryTimeslot.builder()
                                .deliveryTeamId(key.getDeliveryTeamId())
                                .reservationExpiry(expiryTime)
                                .startTime(key.getStartTime())
                                .deliveryDate(key.getDeliveryDate())
                                .build();
                        return shoppingBasketRepository.updateReservedDeliveryTimeslot(customerId, reservedDeliveryTimeslot)
                                .map(basketUpdateResult -> true);
                    } else return Mono.just(false);
                })
                .flatMap(finalResult -> finalResult);
    }

//    public Mono<Void> checkoutShoppingBasket(UUID customerId, String deliveryAddressKey) {
//
//        // retrieve shopping basket
//        shoppingBasketRepository.findById(customerId)
//                .map(basket -> {
//                    // check reserve timeslot
//                    ReservedDeliveryTimeslot reservedDeliveryTimeslot = basket.getReservedDeliveryTimeslot();
//                    if (reservedDeliveryTimeslot.getReservationExpiry().isBefore(dateTimeProvider.now())) {
//                        return Mono.error(new RuntimeException("reserved delivery timeslot already expired"));
//                    } else {
//                        return basket;
//                    }
//                }).map(basket -> {
//                    // construct order
//
//                    // save order
//
//                });
//
//        return null;
//    }

//    private Mono<Void> saveShoppingOrder(ShoppingBasket shoppingBasket, String deliveryAddressKey) {
//        OrderByCustomer orderByCustomer = OrderByCustomer.builder()
//                .customerId(shoppingBasket.getCustomerId())
//                .orderId(UUID.randomUUID())
//                .products(shoppingBasket.getProducts())
//                .deliveryAddressKey(deliveryAddressKey)
//                .submissionTime(dateTimeProvider.now())
//                .status(OrderStatus.SUBMITTED)
//                .build();
//
//        orderByCustomerRepository.save(orderByCustomer);
//
//
//    }




}
