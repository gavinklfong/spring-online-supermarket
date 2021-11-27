package space.gavinklfong.supermarket.repositories;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.Product;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
@SpringBootTest
@Testcontainers
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductRepositoryTest {

    @Container
    public static ElasticsearchContainer container = new ElasticsearchContainer("elasticsearch:7.14.2");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.client.reactive.endpoints", () -> String.format("%s:%d",container.getContainerIpAddress(), container.getMappedPort(9200)));
    }

    private static final String PRODUCT_DEFAULT_NAME = "Default Product Name";
    private static final String DEFAULT_PRODUCT_CATEGORY = "Default Category";
    private static final String PRODUCT_CATEGORY_1 = "Category 1";
    private static final String PRODUCT_CATEGORY_1_1 = "Category 1/Sub Category 1";
    private static final String PRODUCT_CATEGORY_2 = "Category 2";
    private static final String PRODUCT_CATEGORY_2_1 = "Category 2/Sub Category 1";

    private Faker faker = new Faker();

    @Autowired
    private ProductRepository productRepo;

    @AfterEach
    void tearDown() {
        productRepo.deleteAll().block();
    }

    @Test
    void givenNewProduct_whenSave_thenProductSavedSuccessfully() {
        Product savedProduct = addProduct(PRODUCT_DEFAULT_NAME, DEFAULT_PRODUCT_CATEGORY);
        assertNotNull(savedProduct);
    }

    @Test
    void givenProductExists_whenFindById_thenProductIsFound() {
        Product savedProduct = addProduct(PRODUCT_DEFAULT_NAME, DEFAULT_PRODUCT_CATEGORY);
        addProducts(5);

        Mono<Product> retrievedProduct = productRepo.findById(savedProduct.getId());

        Product result = retrievedProduct.block();
        assertEquals(savedProduct, result);
    }

    @Test
    void givenProductsExist_whenFindAll_thenProductCountIsTheSame() {
        List<Product> existingProducts = addProducts(3);

        Flux<Product> products = productRepo.findAll();

        List<Product> result = products.collectList().block();
        assertThat(result).hasSize(existingProducts.size()).hasSameElementsAs(existingProducts);
    }

    @Test
    void givenProductsExist_whenFindByCategory_thenProductCountIsTheSame() {
        List<Product> existingProducts =addProducts(3);
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_2);

        Flux<Product> products = productRepo.findByCategory(DEFAULT_PRODUCT_CATEGORY);

        List<Product> result = products.collectList().block();
        assertThat(result).hasSize(existingProducts.size()).hasSameElementsAs(existingProducts);
    }

    @Test
    void givenProductsExist_whenFindByAnUnknownCategory_thenProductCountIsZero() {
        addProducts(2);
        Flux<Product> books = productRepo.findByCategory("unknown");
        assertEquals(0, books.count().block());
    }

    @Test
    void givenRelevantAndIrrelevantProductsExist_whenFindByRelevantProducts_thenProductCountMatches() {

        // GIVEN
        // 3 relevant products
        List<Product> relevantProducts = asList(
            addProduct("Pepsi Max No Sugar Cola Bottle 3L", PRODUCT_CATEGORY_1),
            addProduct("Pepsi Max No Sugar Cola Can 24x330ml", PRODUCT_CATEGORY_1),
            addProduct("Coca-Cola Zero Sugar 8 x 330ml", PRODUCT_CATEGORY_1)
        );

        // other irrelevant products
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_1_1);
        addProducts(1, PRODUCT_CATEGORY_2);
        addProducts(1, PRODUCT_CATEGORY_2_1);

        // WHEN
        Flux<Product> products = productRepo.findByRelevantProducts("Pepsi Max No Sugar Cola Bottle 3L", PRODUCT_CATEGORY_1);

        // THEN
        List<Product> result = products.collectList().block();
        assertThat(result).hasSize(relevantProducts.size()).hasSameElementsAs(relevantProducts);
    }

    private List<Product> addProducts(int productCount) {
        return addProducts(productCount, DEFAULT_PRODUCT_CATEGORY);
    }

    private List<Product> addProducts(int productCount, String category) {
        return IntStream.range(0, productCount)
                .mapToObj(i -> addProduct(faker.name().name(), category))
                .collect(toList());
    }

    private Product addProduct(String name, String category) {
        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .category(category)
                .brand(faker.company().name())
                .inStock(true)
                .price(faker.number().randomDouble(2, 1, 5))
                .build();
        return productRepo.save(product).block();
    }


}
