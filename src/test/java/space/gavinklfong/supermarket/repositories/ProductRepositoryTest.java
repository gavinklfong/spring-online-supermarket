package space.gavinklfong.supermarket.repositories;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.dtos.SearchResult;
import space.gavinklfong.supermarket.models.Product;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@Testcontainers
public class ProductRepositoryTest {

    @Container
    public static ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.15.0");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.client.reactive.endpoints", () -> String.format("%s:%d", container.getContainerIpAddress(), container.getMappedPort(9200)));
    }

    private static final String DEFAULT_PRODUCT_NAME = "Default Product Name";
    private static final String DEFAULT_PRODUCT_CATEGORY = "Default Category";
    private static final String PRODUCT_CATEGORY_1 = "Category 1";
    private static final String PRODUCT_CATEGORY_1_1 = "Category 1/Sub Category 1";
    private static final String PRODUCT_CATEGORY_2 = "Category 2";
    private static final String PRODUCT_CATEGORY_2_1 = "Category 2/Sub Category 1";

    private final Faker faker = new Faker();

    @Autowired
    private ReactiveElasticsearchClient elasticsearchClient;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        try {
            elasticsearchClient.indices().deleteIndex(new DeleteIndexRequest("products")).block();
        } catch (Exception e) {
            log.warn("fail to delete index");
        }
    }

    @Test
    void addProduct() {
        Product savedProduct = addProduct(DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_CATEGORY);
        assertNotNull(savedProduct);
        log.info("saved product: {}", savedProduct);
    }

    @Test
    void givenProductExists_whenFindById_thenProductIsFound() {
        Product savedProduct = addProduct(DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_CATEGORY);
        addProducts(5);

        Mono<Product> retrievedProduct = productRepository.findById(savedProduct.getId());

        Product result = retrievedProduct.block();
        assertEquals(savedProduct, result);
    }

    @Test
    void givenProductsExist_whenFindByCategory_thenProductCountIsTheSame() {
        List<Product> existingProducts = addProducts(3);
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_2);

        Mono<SearchResult<Product>>  result = productRepository.findByCategory(DEFAULT_PRODUCT_CATEGORY, 0, 50);

        List<Product> outputList = result.block().getItemList();
        assertThat(outputList).hasSize(existingProducts.size()).hasSameElementsAs(existingProducts);
    }

    @Test
    void givenProductsExist_whenFindByCategoryWithAscSortingByPrice_thenResultWithCorrectOrder() {
        // GIVEN
        // other products
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_2);

        // products of the target category
        List<Product> existingProducts = asList(
                addProduct(DEFAULT_PRODUCT_CATEGORY, 3D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 2D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 1.5D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 1.25D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 1D)
        );

        List<Product> expectedProducts = existingProducts.stream().sorted(Comparator.comparingDouble(Product::getPrice)).collect(toList());

        // WHEN
        Mono<SearchResult<Product>> result = productRepository.findByCategory(DEFAULT_PRODUCT_CATEGORY, "price", Sort.Direction.ASC, 0, 50);

        // THEN
        List<Product> outputList = result.block().getItemList();
        assertThat(outputList.equals(expectedProducts)).isTrue();
    }

    @Test
    void givenProductsExist_whenFindByCategoryWithPagination_thenOutputIsASubListWithCorrectOrder() {
        // other products
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_2);

        // products of the target category
        List<Product> existingProducts = asList(
                addProduct(DEFAULT_PRODUCT_CATEGORY, 1D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 2D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 3D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 4D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 5D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 6D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 7D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 8D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 9D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 10D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 11D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 12D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 13D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 14D),
                addProduct(DEFAULT_PRODUCT_CATEGORY, 15D)
                );

        List<Product> sortedProducts = existingProducts.stream().sorted(Comparator.comparingDouble(Product::getPrice).reversed()).collect(toList());
        List<Product> expectedProducts = sortedProducts.subList(5, 10);

        Mono<SearchResult<Product>> result$ = productRepository.findByCategory(DEFAULT_PRODUCT_CATEGORY, "price", Sort.Direction.DESC, 1, 5);

        SearchResult<Product> result = result$.block();
        List<Product> outputList = result.getItemList();
        assertThat(result.getHasNextPage()).isTrue();
        assertThat(result.getNextPageNum()).isEqualTo(2);
        assertThat(result.getNextPageSize()).isEqualTo(5);
        assertThat(outputList).hasSize(5);
        assertThat(outputList.equals(expectedProducts)).isTrue();

    }

    @Test
    void givenProductsExist_whenFindByQuerystring_thenOutputWithCorrectCount() {
        // GIVEN
        // other products
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_2);
        addProducts(1, PRODUCT_CATEGORY_1_1);
        addProducts(1, PRODUCT_CATEGORY_2_1);

        // products with keyword "Default"
        addProducts(1, DEFAULT_PRODUCT_CATEGORY);
        addProducts(1, PRODUCT_CATEGORY_2_1 + " default");
        addProduct("unknown", "unknown", "default brand");
        addProduct(DEFAULT_PRODUCT_NAME, PRODUCT_CATEGORY_1_1);
        addProduct(DEFAULT_PRODUCT_NAME, PRODUCT_CATEGORY_2_1);

        // WHEN
        Mono<SearchResult<Product>> result = productRepository.findByQueryString("Default", 0, 50);

        // THEN
        List<Product> outputList = result.block().getItemList();
        assertThat(outputList).hasSize(5);
    }

    @Test
    void givenProductsExist_whenFindRelevantProducts_thenOutputWithCorrectCount() {
        // GIVEN
        // other products
        addProducts(1, PRODUCT_CATEGORY_1);
        addProducts(1, PRODUCT_CATEGORY_2);
        addProducts(1, PRODUCT_CATEGORY_1_1);
        addProducts(1, PRODUCT_CATEGORY_2_1);
        addProducts(1, PRODUCT_CATEGORY_2_1 + " default");
        addProduct("unknown", "unknown", "default brand");

        // products with keyword "default" in product name or prefix in category
        addProduct(DEFAULT_PRODUCT_NAME, "Drinks / " + PRODUCT_CATEGORY_1_1);
        addProduct(DEFAULT_PRODUCT_NAME, "drinks / " + PRODUCT_CATEGORY_2_1);
        addProduct("default xxxssdd 2334", "drinks / " + PRODUCT_CATEGORY_2_1);
        addProduct("dd33sxxs default dsdf 2334", "drinks / " + PRODUCT_CATEGORY_2_1);
        addProduct("dd33sxxs default", "drinks / " + PRODUCT_CATEGORY_2_1);

        // WHEN
        Mono<SearchResult<Product>> result$ = productRepository.findRelevantProducts("Default", "drinks", null, null,0, 50);

        // THEN
        SearchResult<Product> result = result$.block();
        List<Product> outputList = result.getItemList();
        assertThat(result.getHasNextPage()).isFalse();
        assertThat(result.getNextPageNum()).isNull();
        assertThat(result.getNextPageSize()).isNull();
        assertThat(outputList).hasSize(5);
    }

    @Test
    void givenProductsExist_whenFindByAnUnknownCategory_thenProductCountIsZero() {
        addProducts(2);
        Mono<SearchResult<Product>> result = productRepository.findByCategory("unknown", 0, 50);
        List<Product> outputList = result.block().getItemList();
        assertEquals(0, outputList.size());
    }

    private List<Product> addProducts(int productCount) {
        return addProducts(productCount, DEFAULT_PRODUCT_CATEGORY);
    }

    private List<Product> addProducts(int productCount, String category) {
        return IntStream.range(0, productCount)
                .mapToObj(i -> addProduct(faker.name().name(), category, faker.company().name(), faker.number().randomDouble(2, 1, 5)))
                .collect(toList());
    }

    private Product addProduct(String category, Double price) {
        return addProduct(faker.name().name(), category, faker.company().name(), price);
    }

    private Product addProduct(String name, String category) {
        return addProduct(name, category, faker.company().name(), faker.number().randomDouble(2, 1, 5));
    }

    private Product addProduct(String name, String category, String brand) {
        return addProduct(name, category, brand, faker.number().randomDouble(2, 1, 5));
    }


    private Product addProduct(String name, String category, String brand, Double price) {
        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .category(category)
                .brand(brand)
                .inStock(true)
                .price(price)
                .build();
        return productRepository.save(product).block();
    }
}
