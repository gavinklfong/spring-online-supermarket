package space.gavinklfong.supermarket.services;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import space.gavinklfong.supermarket.dtos.SearchResult;
import space.gavinklfong.supermarket.models.Product;
import space.gavinklfong.supermarket.repositories.ProductRepository;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Slf4j
@SpringJUnitConfig
@ContextConfiguration(classes = {ProductService.class})
@Tag("UnitTest")
public class ProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private final Faker faker = new Faker();

    private final String DEFAULT_PRODUCT_ID = UUID.randomUUID().toString();

    private final String DEFAULT_QUERY_STRING = "Product query";
    private final String DEFAULT_PRODUCT_NAME = "Default product name";
    private final String DEFAULT_PRODUCT_CATEGORY = "Default product category";


    @Test
    void givenProductExists_whenFindById_thenReturnProduct() {

        Product expectedProduct = createProduct();
        when(productRepository.findById(anyString())).thenReturn(Mono.just(expectedProduct));

        Mono<Product> result = productService.getProductById(DEFAULT_PRODUCT_ID);

        StepVerifier
                .create(result)
                .assertNext(product -> assertEquals(expectedProduct, product))
                .verifyComplete();
    }

    @Test
    void givenProductsExist_whenFindByCategory_thenReturnSearchResult() {

        Product expectedProduct = createProduct();
        SearchResult<Product> expectedSearchResult = createSearchResult(asList(expectedProduct));
        when(productRepository.findByCategory(anyString(), anyInt(), anyInt())).thenReturn(Mono.just(expectedSearchResult));

        Mono<SearchResult<Product>> result = productService.getProductsByCategory(DEFAULT_PRODUCT_CATEGORY, 0, 50);

        StepVerifier
                .create(result)
                .assertNext(product -> assertEquals(expectedSearchResult, expectedSearchResult))
                .verifyComplete();
    }

    @Test
    void givenProductsExist_whenFindByRelevantProducts_thenReturnSearchResult() {

        Product expectedProduct = createProduct();
        when(productRepository.findById(DEFAULT_PRODUCT_ID)).thenReturn(Mono.just(expectedProduct));

        List<Product> expectedRelevantProducts = asList(createProduct(), createProduct());
        SearchResult<Product> expectedSearchResult = createSearchResult(expectedRelevantProducts);
        when(productRepository.findRelevantProducts(expectedProduct.getName(), expectedProduct.getCategory(), 0, 50))
                .thenReturn(Mono.just(expectedSearchResult));

        Mono<SearchResult<Product>> result = productService.getRelevantProducts(DEFAULT_PRODUCT_ID, 0, 50);

        StepVerifier
                .create(result)
                .assertNext(searchResult -> assertEquals(expectedSearchResult, searchResult))
                .verifyComplete();
    }

    @Test
    void givenProductsExist_whenFindByQuerystring_thenReturnSearchResult() {

        Product expectedProduct = createProduct();
        SearchResult<Product> expectedSearchResult = createSearchResult(asList(expectedProduct));
        when(productRepository.findByQueryString(anyString(), anyInt(), anyInt())).thenReturn(Mono.just(expectedSearchResult));

        Mono<SearchResult<Product>> result = productService.getProductsByQuerystring(DEFAULT_QUERY_STRING, 0, 50);

        StepVerifier
                .create(result)
                .assertNext(searchResult -> assertEquals(expectedSearchResult, searchResult))
                .verifyComplete();
    }

    private SearchResult<Product> createSearchResult(List<Product> products) {
        return SearchResult.<Product>builder()
                .hasNextPage(false)
                .totalPageNum(1)
                .itemList(products)
                .build();
    }

    private Product createProduct() {
        return   Product.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.commerce().productName())
                .category(faker.lorem().word())
                .brand(faker.company().name())
                .inStock(true)
                .price(faker.number().randomDouble(2, 1, 5))
                .build();
    }


}
