package space.gavinklfong.supermarket.services;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import space.gavinklfong.supermarket.repositories.ProductRepository;

@Slf4j
@SpringBootTest
@Testcontainers
public class ProductServiceTest {

    @Container
    public static ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.15.0");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.client.reactive.endpoints", () -> String.format("%s:%d",container.getContainerIpAddress(), container.getMappedPort(9200)));
    }

    private static final String PRODUCT_CATEGORY = "Drinks";

    private Faker faker = new Faker();

    @Autowired
    private ReactiveElasticsearchOperations elasticsearchOperations;


    @AfterEach
    void tearDown() {

//        elasticsearchOperations.delete(new Query(new Criteria))
    }

}
