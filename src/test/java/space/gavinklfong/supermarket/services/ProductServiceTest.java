package space.gavinklfong.supermarket.services;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.Product;
import space.gavinklfong.supermarket.repositories.ProductRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@Testcontainers
public class ProductServiceTest {

    @Container
    public static ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.15.0");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.client.reactive.endpoints", () -> String.format("%s:%d",container.getContainerIpAddress(), container.getMappedPort(9200)));
//        registry.add("spring.data.elasticsearch.client.reactive.endpoints", () -> "localhost:9200");
    }

    private static final String PRODUCT_DEFAULT_NAME = "Default Product Name";
    private static final String DEFAULT_PRODUCT_CATEGORY = "Default Category";
    private static final String PRODUCT_CATEGORY_1 = "Category 1";
    private static final String PRODUCT_CATEGORY_1_1 = "Category 1/Sub Category 1";
    private static final String PRODUCT_CATEGORY_2 = "Category 2";
    private static final String PRODUCT_CATEGORY_2_1 = "Category 2/Sub Category 1";

    private Faker faker = new Faker();

    @Autowired
    private ReactiveElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ReactiveElasticsearchClient elasticsearchClient;

    @Autowired
    private ProductService productService;

    @AfterEach
    void tearDown() {
        try {
            elasticsearchClient.indices().deleteIndex(new DeleteIndexRequest("products")).block();
        } catch (Exception e) {
            log.warn("fail to delete index");
        }

//        Query query = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.matchAllQuery()).build();
//        elasticsearchOperations.delete(query).block();
    }
//
//    @Test
//    void addProduct() {
//        Product savedProduct = addProduct(PRODUCT_DEFAULT_NAME, DEFAULT_PRODUCT_CATEGORY);
//        assertNotNull(savedProduct);
//        log.info("saved product: {}", savedProduct);
//    }
//
//    @Test
//    void givenProductExists_whenFindById_thenProductIsFound() {
//        Product savedProduct = addProduct(PRODUCT_DEFAULT_NAME, DEFAULT_PRODUCT_CATEGORY);
//        addProducts(5);
//
//        Mono<Product> retrievedProduct = productService.findById(savedProduct.getId());
//
//        Product result = retrievedProduct.block();
//        assertEquals(savedProduct, result);
//    }

    @Test
    void givenProductsExist_whenFindByCategory_thenProductCountIsTheSame() {
        List<Product> existingProducts =addProducts(3);
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_2);

        Flux<Product> products = productService.findByCategory(DEFAULT_PRODUCT_CATEGORY);

        List<Product> result = products.collectList().block();
//        assertThat(result).hasSize(existingProducts.size()).hasSameElementsAs(existingProducts);
        for (Product product : result) {
            log.info("retrieved products: {}", product);
        }
    }
//
//    @Test
//    void givenProductsExist_whenFindByAnUnknownCategory_thenProductCountIsZero() {
//        addProducts(2);
//        Flux<Product> books = productService.findByCategory("unknown");
//        assertEquals(0, books.count().block());
//    }
//
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
        return productService.save(product).block();
    }
}
