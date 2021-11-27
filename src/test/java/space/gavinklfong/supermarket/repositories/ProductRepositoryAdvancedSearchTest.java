package space.gavinklfong.supermarket.repositories;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import space.gavinklfong.supermarket.models.Product;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
@SpringBootTest
//@Testcontainers
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductRepositoryAdvancedSearchTest {

//    @Container
//    public static ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.15.0");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.client.reactive.endpoints", () -> "localhost:9200");
    }

    private static final String PRODUCT_CATEGORY = "Drinks";

    private Faker faker = new Faker();

    @Autowired
    private ProductRepository productRepo;

    @Test
    void givenProductsExist_whenFindByKeyword_thenProductCountIsTheSame() {

        Flux<Product> retrievedProducts = productRepo.findByKeyword("hom*");

        List<Product> result = retrievedProducts.collectList().block();
        result.forEach(product -> log.info("**** retrieved product: {}", product));
    }


    @Test
    void givenProductsExist_whenFindByCategory_thenProductCountIsTheSame() {
//        addProducts(3);
        Flux<Product> products = productRepo.findByCategory("Drinks/Fizzy Drinks/Diet Drinks");
        assertEquals(2, products.count().block());
    }

    @Test
    void givenProductsExist_whenFindByCategoryStartingWith_thenProductCountIsTheSame() {
//        addProducts(3);
        Flux<Product> products = productRepo.findByCategoryStartingWith("Drinks");
        assertEquals(14, products.count().block());
    }

    @Test
    void givenProductsExist_whenFindByNameAndCategoryStartingWith_thenProductCountIsTheSame() {
//        addProducts(3);
        Flux<Product> products = productRepo.findByRelevantProducts("Pepsi Max No Sugar Cola Bottle 3L", "Drinks");
        assertEquals(8, products.count().block());
    }

//    @Test
//    public void givenProductsExist_whenFindByAnUnknownCategory_thenProductCountIsZero() {
//        addProducts(2);
//        Flux<Product> books = productRepo.findByCategory("unknown");
//        assertEquals(0, books.count().block());
//    }

    private void addProducts(int productCount) {
        IntStream.range(0, productCount).forEach(i -> {
            Product product = Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name(faker.name().name())
                    .category(PRODUCT_CATEGORY)
                    .brand(faker.company().name())
                    .inStock(true)
                    .price(faker.number().randomDouble(2, 1, 5))
                    .build();
            Product savedProduct = productRepo.save(product).block();
        });
    }
}
